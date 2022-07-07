package carlos.alves.todotaskreminder.sharedTasks

import android.os.Parcel
import android.os.Parcelable

class SharedTaskInfo() : Parcelable
{
    lateinit var task: String
    var dateTime: String? = null
    var locations: List<String>? = null
    var onLocations: List<String>? = null
    lateinit var userPassword: String
    lateinit var adminPassword: String

    constructor(parcel: Parcel) : this() {
        task = parcel.readString()!!
        dateTime = parcel.readString()
        locations = parcel.createStringArrayList()
        onLocations = parcel.createStringArrayList()
        userPassword = parcel.readString()!!
        adminPassword = parcel.readString()!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(task)
        parcel.writeString(dateTime)
        parcel.writeStringList(locations)
        parcel.writeStringList(onLocations)
        parcel.writeString(userPassword)
        parcel.writeString(adminPassword)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SharedTaskInfo> {
        override fun createFromParcel(parcel: Parcel): SharedTaskInfo {
            return SharedTaskInfo(parcel)
        }

        override fun newArray(size: Int): Array<SharedTaskInfo?> {
            return arrayOfNulls(size)
        }
    }
}