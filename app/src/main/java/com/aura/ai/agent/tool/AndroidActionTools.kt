package com.aura.ai.agent.tool

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.ContactsContract
import android.telephony.SmsManager
import com.aura.ai.core.permissions.PermissionManager
import com.aura.ai.util.ReminderReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class MakeCallTool @Inject constructor(
    @ApplicationContext private val context: Context,
    private val permissions: PermissionManager,
) : Tool {
    override val name = "make_call"
    override val description = "Call a phone number or a contact name."
    override val parameters = mapOf("contact" to "Contact name or phone number")

    override suspend fun execute(args: Map<String, String>): ToolResult {
        val target = args["contact"].orEmpty().trim()
        if (target.isBlank()) return ToolResult(false, "", "contact is required")
        if (!permissions.hasCallPhonePermission()) {
            return ToolResult(false, "", "CALL_PHONE permission is required before I can place a call.")
        }
        val number = resolveNumber(target) ?: return ToolResult(false, "", "No phone number found for '" + target + "'.")
        val sanitized = number.filter { it.isDigit() || it == '+' }
        return runCatching {
            context.startActivity(
                Intent(Intent.ACTION_CALL, Uri.parse("tel:" + sanitized))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
            ToolResult(true, "Call started to " + sanitized)
        }.getOrElse { ToolResult(false, "", it.message ?: "Unable to start the call.") }
    }

    private fun resolveNumber(target: String): String? {
        if (target.any { it.isDigit() } && target.count { it.isDigit() } >= 7) return target
        return ContactLookup(context).findNumber(target)
    }
}

class SendSmsTool @Inject constructor(
    @ApplicationContext private val context: Context,
    private val permissions: PermissionManager,
) : Tool {
    override val name = "send_sms"
    override val description = "Send an SMS message to a phone number or contact."
    override val parameters = mapOf(
        "contact" to "Contact name or phone number",
        "message" to "The SMS text to send",
    )

    override suspend fun execute(args: Map<String, String>): ToolResult {
        val target = args["contact"].orEmpty().trim()
        val message = args["message"].orEmpty()
        if (target.isBlank()) return ToolResult(false, "", "contact is required")
        if (message.isBlank()) return ToolResult(false, "", "message is required")
        if (!permissions.hasSendSmsPermission()) {
            return ToolResult(false, "", "SEND_SMS permission is required before I can send a message.")
        }
        val number = resolveNumber(target) ?: return ToolResult(false, "", "No phone number found for '" + target + "'.")
        val sanitized = number.filter { it.isDigit() || it == '+' }
        return runCatching {
            @Suppress("DEPRECATION")
            val sms = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                context.getSystemService(SmsManager::class.java)
            } else {
                SmsManager.getDefault()
            }
            val parts = sms.divideMessage(message)
            if (parts.size == 1) {
                sms.sendTextMessage(sanitized, null, message, null, null)
            } else {
                sms.sendMultipartTextMessage(sanitized, null, parts, null, null)
            }
            ToolResult(true, "SMS sent to " + sanitized)
        }.getOrElse { ToolResult(false, "", it.message ?: "Unable to send SMS.") }
    }

    private fun resolveNumber(target: String): String? {
        if (target.any { it.isDigit() } && target.count { it.isDigit() } >= 7) return target
        return ContactLookup(context).findNumber(target)
    }
}

class ResolveContactTool @Inject constructor(
    @ApplicationContext private val context: Context,
) : Tool {
    override val name = "resolve_contact"
    override val description = "Look up a contact's phone number without performing an action."
    override val parameters = mapOf("contact" to "Contact name")

    override suspend fun execute(args: Map<String, String>): ToolResult {
        val name = args["contact"].orEmpty().trim()
        if (name.isBlank()) return ToolResult(false, "", "contact is required")
        val number = ContactLookup(context).findNumber(name)
            ?: return ToolResult(false, "", "No phone number found for '" + name + "'.")
        return ToolResult(true, number)
    }
}

class LaunchAppTool @Inject constructor(
    @ApplicationContext private val context: Context,
) : Tool {
    override val name = "launch_app"
    override val description = "Open an installed Android app by its name or package name."
    override val parameters = mapOf("app" to "App name or Android package name")

    override suspend fun execute(args: Map<String, String>): ToolResult {
        val target = args["app"].orEmpty().trim()
        if (target.isBlank()) return ToolResult(false, "", "app is required")
        val pm = context.packageManager
        val packageName = if (target.contains('.')) target else {
            pm.getInstalledApplications(PackageManager.GET_META_DATA)
                .firstOrNull {
                    pm.getApplicationLabel(it).toString().equals(target, ignoreCase = true)
                }?.packageName
        } ?: return ToolResult(false, "", "App '" + target + "' was not found.")
        return runCatching {
            val intent = pm.getLaunchIntentForPackage(packageName)
                ?: error("App '" + target + "' cannot be launched.")
            context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            ToolResult(true, "Opened " + target)
        }.getOrElse { ToolResult(false, "", it.message ?: "Unable to open '" + target + "'.") }
    }
}

class SetReminderTool @Inject constructor(
    @ApplicationContext private val context: Context,
) : Tool {
    override val name = "set_reminder"
    override val description = "Schedule a phone reminder notification at a specific local date and time."
    override val parameters = mapOf(
        "title" to "Short reminder title",
        "datetime" to "Local date/time in yyyy-MM-dd HH:mm",
        "description" to "Optional reminder details",
    )

    override suspend fun execute(args: Map<String, String>): ToolResult {
        val title = args["title"].orEmpty().trim()
        val dateTime = args["datetime"].orEmpty().trim()
        val description = args["description"].orEmpty().trim()
        if (title.isBlank()) return ToolResult(false, "", "title is required")
        if (dateTime.isBlank()) return ToolResult(false, "", "datetime is required")

        val epochMillis = runCatching {
            LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        }.getOrElse {
            return ToolResult(false, "", "datetime must use yyyy-MM-dd HH:mm")
        }

        if (epochMillis <= System.currentTimeMillis()) {
            return ToolResult(false, "", "The reminder time must be in the future.")
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(ReminderReceiver.EXTRA_TITLE, title)
            putExtra(ReminderReceiver.EXTRA_DESCRIPTION, description)
        }
        val requestCode = (epochMillis xor title.hashCode().toLong()).toInt()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        return runCatching {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S &&
                !alarmManager.canScheduleExactAlarms()
            ) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, epochMillis, pendingIntent)
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, epochMillis, pendingIntent)
            }
            ToolResult(true, "Reminder scheduled for " + dateTime)
        }.getOrElse { ToolResult(false, "", it.message ?: "Unable to schedule reminder.") }
    }
}

private class ContactLookup(private val context: Context) {
    fun findNumber(name: String): String? {
        var cursor: android.database.Cursor? = null
        return try {
            cursor = context.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ),
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME.toString() + " LIKE ?",
                arrayOf("%" + name.trim() + "%"),
                null,
            )
            if (cursor?.moveToFirst() == true) {
                val index = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                if (index >= 0) cursor.getString(index) else null
            } else null
        } catch (_: SecurityException) {
            null
        } finally {
            cursor?.close()
        }
    }
}
