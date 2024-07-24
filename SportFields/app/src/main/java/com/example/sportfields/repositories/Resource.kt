package com.example.sportfields.repositories

import java.lang.Exception

sealed class Resource <out R>{
    data class Success<out R>(val result: R): Resource<R>()
    data class Failure(val exception: Exception) : Resource<Nothing>()
    data object loading: Resource<Nothing>()
}