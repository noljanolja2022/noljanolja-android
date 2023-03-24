package com.noljanolja.core.utils

fun <T, E> MutableList<T>.addOrReplace(new: T, map: (T) -> E) {
    var added = false
    val newList = mutableListOf<T>()
    forEach {
        if (map(new) == map(it)) {
            added = true
            newList.add(new)
        } else {
            newList.add(it)
        }
    }
    if (!added) {
        newList.add(new)
    }
    clear()
    addAll(newList)
}