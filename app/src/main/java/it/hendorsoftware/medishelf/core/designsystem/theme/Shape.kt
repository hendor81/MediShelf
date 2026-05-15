package it.hendorsoftware.medishelf.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes

/**
 * Shape Material 3 usate dal tema base dell'app.
 */
val MediShelfShapes = Shapes(
    small = RoundedCornerShape(MediShelfDimens.CornerSmall),
    medium = RoundedCornerShape(MediShelfDimens.CornerMedium),
    large = RoundedCornerShape(MediShelfDimens.CornerLarge),
)
