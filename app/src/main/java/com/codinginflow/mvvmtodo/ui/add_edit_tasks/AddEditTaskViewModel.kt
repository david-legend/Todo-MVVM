package com.codinginflow.mvvmtodo.ui.add_edit_tasks

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codinginflow.mvvmtodo.data.Task
import com.codinginflow.mvvmtodo.data.TaskDao
import com.codinginflow.mvvmtodo.ui.ADD_TASK_RESULT_OK
import com.codinginflow.mvvmtodo.ui.EDIT_TASK_RESULT_OK
import com.codinginflow.mvvmtodo.ui.tasks.TaskViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AddEditTaskViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {

    val task = state.get<Task>("task")

    var taskName = state.get<String>("taskName") ?: task?.name ?: ""
        set(value) {
            field = value
            state.set<String>("taskName", value)
        }

    var taskImportance = state.get<Boolean>("taskImportance") ?: task?.important ?: false
        set(value) {
            field = value
            state.set("taskImportance", value)
        }

    private val addEditTaskChannel = Channel<AddEditTaskEvents>()
    var addEditTaskEvent = addEditTaskChannel.receiveAsFlow()

    fun onSaveClick() {
        if (taskName.isBlank()) {
            showValidInputMessage("Name cannot be empty")
            return
        }

        if (task != null) {
            val updatedTask = task.copy(name = taskName, important = taskImportance)
            updateTask(updatedTask)
        } else {
            val newTask = Task(name = taskName, important = taskImportance)
            createTask(newTask)
        }
    }

    private fun updateTask(task: Task) = viewModelScope.launch {
        taskDao.update(task)
        addEditTaskChannel.send(AddEditTaskEvents.NavigateBackWithResult(EDIT_TASK_RESULT_OK))
    }

    private fun createTask(task: Task) = viewModelScope.launch {
        taskDao.insert(task)
        addEditTaskChannel.send(AddEditTaskEvents.NavigateBackWithResult(ADD_TASK_RESULT_OK))
    }

    private fun showValidInputMessage(message: String) = viewModelScope.launch {
        addEditTaskChannel.send(AddEditTaskEvents.ShowInvalidInputMessage(message))

    }

    sealed class AddEditTaskEvents {
        data class ShowInvalidInputMessage(val message: String) : AddEditTaskEvents()
        data class NavigateBackWithResult(val result: Int) : AddEditTaskEvents()
    }

}