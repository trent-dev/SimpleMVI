package com.devtrentoh.sampleapp.ui.main

import com.devtrentoh.sampleapp.ui.main.mvi.components.TodoItem
import java.lang.NullPointerException

class MainModel {

    private var currentTodoItemList = mutableListOf<TodoItem>()

    fun addItem(description: String): TodoItem {
        val newItem = TodoItem(findAvailableIndex(), description)
        currentTodoItemList.add(newItem)
        syncDB()
        return newItem
    }

    fun editItem(editTarget: TodoItem, newDescription: String): TodoItem {
        val index = currentTodoItemList.indexOf(editTarget)

        val newItem = editTarget.copy(description = newDescription)
        currentTodoItemList[index] = newItem

        syncDB()
        return newItem
    }

    fun toggleDone(todoItem: TodoItem) {
        val target = todoItem ?: throw NullPointerException("No Edit Target Set")

        val index = currentTodoItemList.indexOf(target)

        currentTodoItemList[index] = target.copy(isDone = !target.isDone)
    }

    fun getItemList(): List<TodoItem> = currentTodoItemList

    private fun findAvailableIndex(): Int {
        val sortedTodoItems = currentTodoItemList.toList().sortedBy { it.uniqueIndex }
        var index = 0
        for (todoItem in sortedTodoItems) {
            if (todoItem.uniqueIndex != index) return index
            index++
        }
        return index
    }

    private fun syncDB() {
        // TODO("Make this asychronous but ordered")
    }
}