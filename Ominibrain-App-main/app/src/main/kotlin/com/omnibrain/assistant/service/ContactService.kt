package com.omnibrain.assistant.service

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import com.omnibrain.assistant.util.Constants

class ContactService(private val context: Context) {
    
    fun parseAndCallContact(command: String): String {
        val lowerCommand = command.lowercase()
        
        // Check if it's a call command
        val isCallCommand = lowerCommand.contains(Constants.COMMAND_CALL) ||
                lowerCommand.contains(Constants.COMMAND_PHONE) ||
                lowerCommand.contains(Constants.COMMAND_DIAL)
        
        if (!isCallCommand) {
            return "" // Not a call command
        }
        
        // Extract contact name
        val contactName = extractContactName(lowerCommand)
        if (contactName.isEmpty()) {
            return "I couldn't identify who to call."
        }
        
        // Try to call the contact
        return callContact(contactName)
    }
    
    private fun extractContactName(command: String): String {
        val keywords = listOf(Constants.COMMAND_CALL, Constants.COMMAND_PHONE, Constants.COMMAND_DIAL)
        var contactName = command
        
        keywords.forEach { keyword ->
            if (contactName.contains(keyword)) {
                contactName = contactName.substringAfter(keyword).trim()
            }
        }
        
        return contactName
    }
    
    private fun callContact(contactName: String): String {
        val phoneNumber = getContactPhoneNumber(contactName)
        
        return if (phoneNumber != null) {
            try {
                val intent = Intent(Intent.ACTION_CALL).apply {
                    data = Uri.parse("tel:$phoneNumber")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
                "Calling $contactName"
            } catch (e: SecurityException) {
                "Phone permission required to make calls"
            } catch (e: Exception) {
                "Error making call: ${e.message}"
            }
        } else {
            "Contact '$contactName' not found in your contacts"
        }
    }
    
    private fun getContactPhoneNumber(contactName: String): String? {
        val cursor = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
            ),
            null,
            null,
            null
        )
        
        cursor?.use {
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            
            while (it.moveToNext()) {
                val name = it.getString(nameIndex)
                val number = it.getString(numberIndex)
                
                // Fuzzy match contact name
                if (name.lowercase().contains(contactName.lowercase()) ||
                    contactName.lowercase().contains(name.lowercase())) {
                    return number
                }
            }
        }
        
        return null
    }
}
