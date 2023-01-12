package ua.alegator1209.voltpolska.utils

import kotlinx.coroutines.flow.MutableSharedFlow

suspend fun MutableSharedFlow<Unit>.fire() = emit(Unit)
