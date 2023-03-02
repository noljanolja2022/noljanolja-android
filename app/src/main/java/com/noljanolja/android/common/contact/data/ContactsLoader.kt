package com.noljanolja.android.common.contact.data

import android.content.ContentResolver
import android.content.Context
import android.provider.ContactsContract
import com.noljanolja.android.common.contact.domain.model.Contact
import com.noljanolja.android.services.PermissionChecker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class ContactsLoader(
    context: Context,
) {
    private val resolver: ContentResolver = context.contentResolver
    private val permissionChecker: PermissionChecker = PermissionChecker(context)

    private val projections = arrayOf(
        ContactsContract.Contacts._ID,
        ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
    )

    fun loadContacts(): Flow<Contact> = flow {
        if (permissionChecker.canReadContacts()) {
            resolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                projections,
                null,
                null,
                null,
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    do {
                        val contactId = cursor.getColumnIndex(projections[0])
                            .takeIf { it >= 0 }?.let { cursor.getLong(it) } ?: 0
                        val name = cursor.getColumnIndex(projections[1])
                            .takeIf { it >= 0 }?.let { cursor.getString(it) } ?: ""
                        val phones = retrievePhoneNumber(contactId)
                        val emails = retrieveEmail(contactId)

                        Contact(contactId, name, phones, emails)
                            .takeIf { phones.isNotEmpty() || emails.isNotEmpty() }?.let { emit(it) }
                    } while (cursor.moveToNext())
                }
            }
        }
    }.flowOn(Dispatchers.Default)

    private fun retrievePhoneNumber(contactId: Long): List<String> {
        val result = mutableListOf<String>()
        resolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
            arrayOf(contactId.toString()),
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER).takeIf { it >= 0 }?.let {
                        result.add(cursor.getString(it))
                    }
                } while (cursor.moveToNext())
            }
        }
        return result
    }

    private fun retrieveEmail(contactId: Long): List<String> {
        val result = mutableListOf<String>()
        resolver.query(
            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
            null,
            "${ContactsContract.CommonDataKinds.Email.CONTACT_ID} = ?",
            arrayOf(contactId.toString()),
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA).takeIf { it >= 0 }?.let {
                        result.add(cursor.getString(it))
                    }
                } while (cursor.moveToNext())
            }
        }
        return result
    }
}