@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlacaDetailsBottomSheet(
    visible: Boolean,
    placa: PlacaItemResponse?,
    onDismiss: () -> Unit
) {
    if (visible && placa != null) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            PlacaDetailsCard(
                placa = placa,
                onDismiss = onDismiss
            )
        }
    }
}
