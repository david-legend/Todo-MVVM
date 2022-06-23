package com.codinginflow.mvvmtodo.ui.add_edit_tasks

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.codinginflow.mvvmtodo.R
import com.codinginflow.mvvmtodo.databinding.FragmentAddEditTaskBinding
import com.codinginflow.mvvmtodo.util.exhaustive
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_add_edit_task.*
import kotlinx.android.synthetic.main.fragment_tasks.*
import kotlinx.coroutines.flow.collect

const val ADD_EDIT_REQUEST = "add_edit_request"

@AndroidEntryPoint
class AddEditFragment : Fragment(R.layout.fragment_add_edit_task) {

    private val viewModel: AddEditTaskViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentAddEditTaskBinding.bind(view)
        binding.apply {
            task_name_edit_text.setText(viewModel.taskName)
            checkboxImportant.isChecked = viewModel.taskImportance
            checkboxImportant.jumpDrawablesToCurrentState()
            taskDateCreated.isVisible = viewModel.task != null
            taskDateCreated.text = "Created: ${viewModel.task?.createdDateFormatted}"

            task_name_edit_text.addTextChangedListener {
                viewModel.taskName = it.toString()
            }

            checkboxImportant.setOnCheckedChangeListener { _, isChecked ->
                viewModel.taskImportance = isChecked
            }

            addEditFab.setOnClickListener {
                viewModel.onSaveClick()
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.addEditTaskEvent.collect { event ->
                    when (event) {
                        is AddEditTaskViewModel.AddEditTaskEvents.ShowInvalidInputMessage -> {
                            Snackbar.make(requireView(), event.message, Snackbar.LENGTH_LONG).show()
                        }
                        is AddEditTaskViewModel.AddEditTaskEvents.NavigateBackWithResult -> {
                            binding.taskNameEditText.clearFocus()
                            setFragmentResult(
                                ADD_EDIT_REQUEST,
                                bundleOf(ADD_EDIT_REQUEST to event.result)
                            )
                            findNavController().popBackStack()
                        }

                    }.exhaustive
                }
            }
        }
    }
}