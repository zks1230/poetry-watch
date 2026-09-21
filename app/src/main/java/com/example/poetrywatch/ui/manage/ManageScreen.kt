package com.example.poetrywatch.ui.manage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageScreen(viewModel: ManageViewModel = hiltViewModel()) {
    var showForm by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text(if (showForm) "录入诗词" else "内容管理") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (!showForm) {
                Text(
                    text = "内置库已含初中人教版必背诗词，可在此录入个人诗词。",
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 13.sp
                )
                Card(
                    onClick = { showForm = true },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Text("录入新诗词", modifier = Modifier.padding(16.dp))
                }
                Card(
                    onClick = { /* 预留联网更新 */ },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Text("联网更新诗词库（预留）", modifier = Modifier.padding(16.dp))
                }
            } else {
                var title by rememberSaveable { mutableStateOf("") }
                var author by rememberSaveable { mutableStateOf("") }
                var dynasty by rememberSaveable { mutableStateOf("") }
                var grade by rememberSaveable { mutableStateOf("") }
                var content by rememberSaveable { mutableStateOf("") }
                var translation by rememberSaveable { mutableStateOf("") }

                Label("诗题")
                OutlinedTextField(value = title, onValueChange = { title = it }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Label("作者")
                OutlinedTextField(value = author, onValueChange = { author = it }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Label("朝代")
                OutlinedTextField(value = dynasty, onValueChange = { dynasty = it }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Label("年级（如：七年级上册）")
                OutlinedTextField(value = grade, onValueChange = { grade = it }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Label("正文（每句换行）")
                OutlinedTextField(value = content, onValueChange = { content = it }, modifier = Modifier.fillMaxWidth())
                Label("译文")
                OutlinedTextField(value = translation, onValueChange = { translation = it }, modifier = Modifier.fillMaxWidth())

                Button(
                    onClick = {
                        viewModel.addUserPoem(title, author, dynasty, content, translation, grade)
                        showForm = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = title.isNotBlank() && content.isNotBlank()
                ) { Text("保存") }
                OutlinedButton(
                    onClick = { showForm = false },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("返回") }
            }
        }
    }
}

@Composable
private fun Label(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        color = MaterialTheme.colorScheme.secondary,
        fontWeight = FontWeight.Medium
    )
}