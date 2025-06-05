package com.rkbapps.autoreply.notificationhelper

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import java.util.ArrayList

class Action : Parcelable {
    private val text: String?
    private val packageName: String?
    private val p: PendingIntent?
    private val isQuickReply: Boolean
    private val remoteInputs = ArrayList<RemoteInputParcel>()

    constructor(value: Parcel) {
        text = value.readString()
        packageName = value.readString()
        p = value.readTypedParcelableCompat<PendingIntent>()
        isQuickReply = value.readByte().toInt() != 0
        value.readTypedList(remoteInputs, RemoteInputParcel)
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(text)
        dest.writeString(packageName)
        dest.writeParcelable(p, flags)
        dest.writeByte((if (isQuickReply) 1 else 0).toByte())
        dest.writeTypedList(remoteInputs)
    }

    constructor(action: NotificationCompat.Action, packageName: String?, isQuickReply: Boolean) {
        text = action.title.toString()
        this.packageName = packageName
        p = action.actionIntent
        if (action.remoteInputs != null) {
            val size = action.remoteInputs!!.size
            for (i in 0 until size) remoteInputs.add(RemoteInputParcel(action.remoteInputs!![i]))
        }
        this.isQuickReply = isQuickReply
    }

    @Throws(PendingIntent.CanceledException::class)
    fun sendReply(context: Context?, msg: String?) {
        Log.d("Action::sendReply","inside sendReply")
        val intent = Intent()
        val bundle = Bundle()
        val actualInputs = ArrayList<RemoteInput>()
        for (input in remoteInputs) {
            Log.d("Action::sendReply","RemoteInput: ${input.label}",)
            bundle.putCharSequence(input.resultKey, msg)
            val builder = RemoteInput.Builder(input.resultKey.toString())
            builder.setLabel(input.label)
            builder.setChoices(input.getChoices())
            builder.setAllowFreeFormInput(input.isAllowFreeFormInput)
            builder.addExtras(input.extras!!)
            actualInputs.add(builder.build())
        }
        val inputs = actualInputs.toTypedArray()
        RemoteInput.addResultsToIntent(inputs, intent, bundle)
        p!!.send(context, 0, intent)
    }


    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Action> {
        override fun createFromParcel(parcel: Parcel): Action {
            return Action(parcel)
        }

        override fun newArray(size: Int): Array<Action?> {
            return arrayOfNulls(size)
        }
    }




}

inline fun <reified T : Parcelable> Parcel.readTypedParcelableCompat(): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        readParcelable(T::class.java.classLoader, T::class.java)
    } else {
        @Suppress("DEPRECATION")
        readParcelable(T::class.java.classLoader)
    }
}
