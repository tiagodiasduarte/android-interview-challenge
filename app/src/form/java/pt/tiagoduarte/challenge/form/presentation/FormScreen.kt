package pt.tiagoduarte.challenge.form.presentation

import androidx.annotation.StringRes
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pt.tiagoduarte.challenge.R
import pt.tiagoduarte.challenge.form.model.RatingClassification
import pt.tiagoduarte.challenge.form.validation.FormError
import pt.tiagoduarte.challenge.ui.theme.AppTheme
import pt.tiagoduarte.challenge.ui.theme.PreviewDevices
import pt.tiagoduarte.challenge.ui.theme.SpaceSize
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

private val MaxFormWidth = 480.dp

@Composable
fun FormRoute(viewModel: FormViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    FormScreen(
        uiState = uiState,
        today = viewModel.today,
        actions = FormActions(
            onNameChange = viewModel::onNameChange,
            onEmailChange = viewModel::onEmailChange,
            onNumberChange = viewModel::onNumberChange,
            onPromoCodeChange = viewModel::onPromoCodeChange,
            onDeliveryDateChange = viewModel::onDeliveryDateChange,
            onRatingChange = viewModel::onRatingChange,
            onSubmit = viewModel::onSubmit,
            onSubmittedMessageShown = viewModel::onSubmittedMessageShown,
        ),
    )
}

private data class FormActions(
    val onNameChange: (String) -> Unit = {},
    val onEmailChange: (String) -> Unit = {},
    val onNumberChange: (String) -> Unit = {},
    val onPromoCodeChange: (String) -> Unit = {},
    val onDeliveryDateChange: (LocalDate) -> Unit = {},
    val onRatingChange: (RatingClassification) -> Unit = {},
    val onSubmit: () -> Unit = {},
    val onSubmittedMessageShown: () -> Unit = {},
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormScreen(uiState: FormUiState, today: LocalDate, actions: FormActions) {
    val snackbarHostState = remember { SnackbarHostState() }
    val submittedMessage = stringResource(R.string.form_submitted)

    LaunchedEffect(uiState.isSubmitted) {
        if (uiState.isSubmitted) {
            snackbarHostState.showSnackbar(submittedMessage)
            actions.onSubmittedMessageShown()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.form_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(SpaceSize.large),
            contentAlignment = Alignment.TopCenter,
        ) {
            FormFields(
                uiState = uiState,
                today = today,
                actions = actions,
                modifier = Modifier.widthIn(max = MaxFormWidth),
            )
        }
    }
}

@Composable
private fun FormFields(uiState: FormUiState, today: LocalDate, actions: FormActions, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(SpaceSize.medium),
    ) {
        FormTextField(
            value = uiState.name,
            onValueChange = actions.onNameChange,
            label = R.string.form_name,
            error = uiState.errors.name,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
        )
        FormTextField(
            value = uiState.email,
            onValueChange = actions.onEmailChange,
            label = R.string.form_email,
            error = uiState.errors.email,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        )
        FormTextField(
            value = uiState.number,
            onValueChange = actions.onNumberChange,
            label = R.string.form_number,
            error = uiState.errors.number,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
        FormTextField(
            value = uiState.promoCode,
            onValueChange = actions.onPromoCodeChange,
            label = R.string.form_promo_code,
            error = uiState.errors.promoCode,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
        )
        DeliveryDateField(
            date = uiState.deliveryDate,
            today = today,
            onDateChange = actions.onDeliveryDateChange,
            error = uiState.errors.deliveryDate,
        )
        RatingField(
            rating = uiState.rating,
            onRatingChange = actions.onRatingChange,
            error = uiState.errors.rating,
        )
        Button(
            onClick = actions.onSubmit,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = SpaceSize.medium),
        ) {
            Text(stringResource(R.string.form_submit))
        }
    }
}

@Composable
private fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    @StringRes label: Int,
    error: FormError?,
    keyboardOptions: KeyboardOptions,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(label)) },
        isError = error != null,
        supportingText = error?.let { { Text(stringResource(it.messageRes())) } },
        singleLine = true,
        keyboardOptions = keyboardOptions,
        modifier = modifier.fillMaxWidth(),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeliveryDateField(
    date: LocalDate?,
    today: LocalDate,
    onDateChange: (LocalDate) -> Unit,
    error: FormError?,
    modifier: Modifier = Modifier,
) {
    var showPicker by rememberSaveable { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Release) showPicker = true
        }
    }

    OutlinedTextField(
        value = date?.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)).orEmpty(),
        onValueChange = {},
        readOnly = true,
        label = { Text(stringResource(R.string.form_delivery_date)) },
        trailingIcon = { Icon(imageVector = Icons.Filled.CalendarMonth, contentDescription = null) },
        isError = error != null,
        supportingText = error?.let { { Text(stringResource(it.messageRes())) } },
        singleLine = true,
        interactionSource = interactionSource,
        modifier = modifier.fillMaxWidth(),
    )

    if (showPicker) {
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = date?.toUtcMillis(),
            initialDisplayedMonthMillis = (date ?: today).toUtcMillis(),
            selectableDates = DeliverySelectableDates(today),
        )
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        pickerState.selectedDateMillis?.let { onDateChange(it.toLocalDate()) }
                        showPicker = false
                    },
                ) {
                    Text(stringResource(R.string.form_date_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) {
                    Text(stringResource(R.string.form_date_cancel))
                }
            },
        ) {
            DatePicker(state = pickerState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RatingField(
    rating: RatingClassification?,
    onRatingChange: (RatingClassification) -> Unit,
    error: FormError?,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = rating?.let { stringResource(it.labelRes()) }.orEmpty(),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.form_rating)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            isError = error != null,
            supportingText = error?.let { { Text(stringResource(it.messageRes())) } },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            RatingClassification.entries.forEach { option ->
                DropdownMenuItem(
                    text = { Text(stringResource(option.labelRes())) },
                    onClick = {
                        onRatingChange(option)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}

@PreviewDevices
@Composable
private fun FormEmptyPreview() {
    AppTheme {
        FormScreen(uiState = FormUiState(), today = LocalDate.of(2026, 9, 23), actions = FormActions())
    }
}

@PreviewDevices
@Composable
private fun FormErrorsPreview() {
    AppTheme {
        FormScreen(
            uiState = FormUiState(
                email = "tiago@",
                promoCode = "promo",
                errors = FormErrors(
                    name = FormError.EMPTY,
                    email = FormError.INVALID_EMAIL,
                    number = FormError.EMPTY,
                    promoCode = FormError.PROMO_CODE_CHARACTERS,
                    deliveryDate = FormError.EMPTY,
                    rating = FormError.EMPTY,
                ),
            ),
            today = LocalDate.of(2026, 9, 23),
            actions = FormActions(),
        )
    }
}
