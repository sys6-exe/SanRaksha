package com.example.sanraksha.front

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Patient-table")
data class Patient(
    @PrimaryKey(autoGenerate = true )
    val id:Long = 0L,
    @ColumnInfo(name = "Patient-title")
    val name:String = "",
    @ColumnInfo(name = "patient-week")
    val pregnancyWeek : Int = 0,
    @ColumnInfo(name = "last-Checkup-date")
    val lastCheckup : String = ""
)
