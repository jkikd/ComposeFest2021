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

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.codelabs.state.ui.StateCodelabTheme

class TodoActivity : AppCompatActivity() {

    val todoViewModel by viewModels<TodoViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StateCodelabTheme {
                Surface {
                    // TODO: build the screen in compose
                    TodoActvityScreen(todoViewModel)
                }
            }
        }
    }


    //실습 week2-2
    @Composable
    fun TodoScreen(
        items: List<TodoItem>,
        onAddItem: (TodoItem) -> Unit,
        onRemoveItem: (TodoItem) -> Unit,
        onStartEdit: (TodoItem) -> Unit,
        onEditItemChange: (TodoItem) -> Unit,
        onEditDone: () -> Unit
    ){
        Column{
            val enableTopSection = currentlyEditing == null
            //백그라운드에서 호출하도록
            TodoItemInputBackground(elevate = enableTopSection/*, modifier = Modifier.fillMaxWidth()*/) {
                if(enableTopSection){
                    TodoItemEntryInput(onAddItem)
                }else{
                    Text(
                        "Editing item",
                        style = MaterialTheme.typography.h6,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(16.dp)
                            .fillMaxWidth()
                    )
                }
            }
        }
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(top = 8.dp)
        ){
            items(items){todo ->
                if(currentlyEditing?.id == todo.id){
                    TodoItemInlineEditor(
                        itme = currentlyEditing,
                        onEditItemChange = onEdititemChange,
                        onEditDone = onEditDone,
                        onRemoveItem = {onRemoveItem(todo)}
                    )
                }else{
                    TodoRow(
                        todo,
                        {onStartEdit(it),
                        Modifier.fillParentMaxWidth()}
                    )
                }
            }
        }
    }

    @Composable
    fun TodoRow(
        todo: TodoItem,
        onItemClicked: (TodoItem) -> Unit,
        modifier: Modifier = Modifier,
        iconAlpha: Float = remember(todo.id){randomTint()}
    ){
        Row(
           modifier = modifier
               .clickable { onItemClicked(todo) }
               .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            //아이콘 임의 강조
            Text(todo.task)
            /*val iconAlph : Float = remember(todo.id){randomTint()}임의의 색 변경*/
            Icon(
                imageVector = todo.icon.imageVector,
                tint = LocalContentColor.current.copy(alpha = iconAlpha),
                contentDescription = stringResource(id = todo.icon.contentDescription)
            )
        }
    }

    //텍스트 삽임
    @Composable
    fun TodoInputTextField(
        text: String,
        onTextChange: (String) -> Unit,
        modifier: Modifier
    ){
        //val(text, setText) = remember(mutableStateOf("")}
        TodoInputText(text, onTextChange, modifier)
    }

    //아이템 삽입을 표시
    @Composable
    fun TodoItemInput(//onItemComplete: (TodoItem) -> Unit
        text: String,
        onTextChange: (String) -> Unit,
        icon: TodoIcon,
        onIconChange: (TodoIcon) -> Unit,
        submit: () -> Unit,
        iconsVisible: Boolean,
        buttonSlot: @Composable() -> Unit
    ){
        /*val(text, setText) = remember{mutableStateOf("")}
        val(icon, setIcon) = remember{mutableStateOf(TodoIcon.Default)}
        val iconsVisible = text.isNotBlank()
        val submit = {
            onItemComplete(TodoItem(text,icon))
            setIcon(TodoIcon.Default)
            setText("")*/
        }
        Column{
            Row(
                Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp)
            ){
                TodoInputTextField(
                    text = text,
                    onTextChange, //= setText,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    //onImeAction = submit
                    submit
                )

                Spacer(modifier = Modifier.width(8.dp))
                Box(Modifier.align(Alignment.CenterVertically)){buttonSlot()}
                /*TodoEditButton(
                    onClick = submit,
                        *//*{onItemComplete(TodoItem(text, icon))
                        setIcon(TodoIcon.Default)
                        setText("")}*//*
                    text = "Add",
                    modifier = Modifier.align(Alignment.CenterVertically),
                    enabled= text.isNotBlank()
                )*/
            }
            if(iconsVisible){
                AnimatedIconRow(icon, setIcon, Modifier.padding(top = 8.dp))
            } else{
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
    //키보드 작업 처리를 위해 사용하도록
    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun TodoInputText(
        text: String,
        onTextChange: (String) -> Unit,
        modifier: Modifier = Modifier,
        onImeAction: () -> Unit = {}
    ){
        val keyboardController = LocalSoftwareKeyboardController.current
        TextField(
            value = text,
            onValueChange = onTextChange,
            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
            maxLine = 1,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardOptions(onDone = {
                onImeAction()
                keyboardController?.hide()
            }),
            modifier = modifier
        )
    }

    @Composable
    private fun TodoActvityScreen(todoViewModel: TodoViewModel){
        val items: List<TodoItem> by todoViewModel.todoItems.observeAsState(listOf())
        TodoScreen(
            items = items,
            onAddItem = {todoViewModel.addItem(it)},
            onRemoveItem = {todoViewModel.removeItem(it)}
        )
    }

    //리팩터링 작업 후 추가 부분
    @Composable
    fun TodoItemEntryInput(onItemComplete: (TodoItem) -> Unit){
        val(text, setText) = remember{mutableStateOf("")}
        val(icon, setIcon) = remember{ mutableStateOf(TodoIcon.Default)}
        //val iconsVisible = text.isNotBlank()
        val submit = {
            /*onItemComplete(TodoItem(text, icon))
            setIcon(TodoIcon.Default)
            setText("")*/
            if(text.isNotBlank()) {
                onItemComplete(TodoItem(text, icon))
                onTextChange("")
                onIconChange(TodoIcon.Default)
            }
        }
        TodoItemInput(
            text = text,
            onTextChange = setText,
            icon = icon,
            onIconChange = setIcon,
            submit = submit,
            iconsVisible = text.isNotBlank()/*iconsVisible*/
        ){
            TodoEditButton(onClick = submit, text = "Add", enabled = text.isNotBlank())
        }
    }

    @Composable
    fun TodoItemInlineEditor(
        item:TodoItem,
        onEditItemChange: (TodoItem) -> Unit,
        onEditDone: () -> Unit,
        onRemoveItem: () -> Unit
    ) = TodoItemInput(
        test = item.task,
        onTextChange = {onEditItemChange(item.copy(task = it))},
        icon = item.icon,
        onIconChange = {onEditItemChange(item.copy(icon = it))},
        submit = onEditDone,
        iconsVisible = true,
        buttonSlot = {
            Row{
                val shrinkButtons = Modifier.widthln(20.dp)
                TextButton(onClick = onEditDone, modifier = shrinkButtons){
                    Text(
                        text = "\uD83D\uDCBE",//floppy disk
                        textAlign = TextAlign.End,
                        modifier = Modifier.width(30.dp)
                    )
                }
                TextButton(onClick = onRemoveItem, modifier = shrinkButtons){
                    Text(
                        text = "X",
                        textAlign = TextAlign.End,
                        modifier = Modifier.width(30.dp)
                    )
                }
            }
        }
    )

    @Preview
    @Composable
    fun PreviewTodoItemInput() = TodoItemInput(onItemComplete = {})
}
