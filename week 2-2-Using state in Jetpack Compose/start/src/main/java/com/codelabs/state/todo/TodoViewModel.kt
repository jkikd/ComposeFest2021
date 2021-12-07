/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codelabs.state.todo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TodoViewModel : ViewModel() {
    //state
    //private var _todoItems = MutableLiveData(listOf<TodoItem>())
    //val todoItems: LiveData<List<TodoItem>> = _todoItems

    //private state
    private var currentEditPosition by mutableStateOf(-1)

    //state - todoItems
    var todoItems = mutableStateListOf<TodoItem>()
    private set

    //state
    var currentEditItem:TodoItem?.get() = todoItems.getOrNull(currentEditPosition)
    // event - 아이템 추가
    fun addItem(item: TodoItem) {
        //_todoItems.value = _todoItems.value!! + listOf(item)
        todoItems.add(item)
    }
    //event - 아이템 제거
    fun removeItem(item: TodoItem) {
        /*_todoItems.value = _todoItems.value!!.toMutableList().also {
            it.remove(item)
        }*/
        todoItems.remove(item)
        onEditDone()//항목제거시 종료
    }

    //event - onEditDone
    fun onEditDone(){
        currentEditPosition = -1
    }

    //event - onEditItemChange
    fun onEditItemChange(item: TodoItem){
        val currentItem = requireNotNull(currentEditItem)
        require(currentItem.id==item.id){
            "You can only change an item with the same id as currentEditItem"
        }
        todoItems[currentEditPosition] = item
    }
}
