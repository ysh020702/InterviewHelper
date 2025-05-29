package com.haedal.interviewhelper.domain.repository

import java.io.File

interface AudioRecorderRepository {
    fun start(fileName: String): File
    fun stop(): File
}