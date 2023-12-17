package com.noljanolja.android.extensions

fun <T> List<T>?.convertToMutableList(): MutableList<T> = this?.toMutableList() ?: mutableListOf()

fun <T> List<T>?.convertToArrayList(): ArrayList<T> = this as? ArrayList<T> ?: arrayListOf()

fun <T> List<T>?.getListDataNotNull(): List<T> = this ?: listOf()

fun <T> MutableList<T>.updateListData(newList: List<T>) {
    clear()
    addAll(newList)
}
