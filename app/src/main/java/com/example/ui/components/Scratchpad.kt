package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900

data class DrawnStroke(
    val path: Path,
    val color: Color,
    val strokeWidth: Float,
    val isHighlighter: Boolean = false
)

@Composable
fun ScratchpadCard(
    modifier: Modifier = Modifier,
    title: String = "Matematik Karalama Tahtası",
    initialHeightDp: Int = 260,
    onClose: (() -> Unit)? = null
) {
    val strokes = remember { mutableStateListOf<DrawnStroke>() }
    var currentPath by remember { mutableStateOf<Path?>(null) }
    var currentColor by remember { mutableStateOf(Color(0xFFFBBF24)) } // Amber Gold default
    var strokeWidth by remember { mutableFloatStateOf(4f) }
    var isEraser by remember { mutableStateOf(false) }
    var isHighlighter by remember { mutableStateOf(false) }
    var showGrid by remember { mutableStateOf(true) }

    val colors = listOf(
        Color(0xFFFBBF24), // Gold
        Color(0xFF38BDF8), // Cyan
        Color(0xFF34D399), // Emerald
        Color(0xFFF43F5E), // Coral
        Color(0xFFFFFFFF), // White
        Color(0xFFA855F7)  // Purple
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("scratchpad_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Slate900),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Create,
                        contentDescription = "Karalama",
                        tint = Color(0xFFFBBF24),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 14.sp,
                        style = MaterialTheme.typography.titleSmall
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Grid Toggle
                    IconButton(
                        onClick = { showGrid = !showGrid },
                        modifier = Modifier.size(32.dp).testTag("grid_toggle")
                    ) {
                        Icon(
                            imageVector = Icons.Default.GridOn,
                            contentDescription = "Kareli Defter",
                            tint = if (showGrid) Color(0xFF38BDF8) else Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Undo
                    IconButton(
                        onClick = {
                            if (strokes.isNotEmpty()) strokes.removeAt(strokes.lastIndex)
                        },
                        enabled = strokes.isNotEmpty(),
                        modifier = Modifier.size(32.dp).testTag("undo_stroke")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Undo,
                            contentDescription = "Geri Al",
                            tint = if (strokes.isNotEmpty()) Color.White else Color.DarkGray,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Clear
                    IconButton(
                        onClick = { strokes.clear() },
                        modifier = Modifier.size(32.dp).testTag("clear_scratchpad")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Temizle",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    if (onClose != null) {
                        IconButton(
                            onClick = onClose,
                            modifier = Modifier.size(32.dp).testTag("close_scratchpad")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Kapat",
                                tint = Color.LightGray,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Canvas drawing area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(initialHeightDp.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Slate800)
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
                    .testTag("drawing_canvas")
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(isEraser, currentColor, strokeWidth, isHighlighter) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    val newPath = Path().apply { moveTo(offset.x, offset.y) }
                                    currentPath = newPath
                                },
                                onDrag = { change, _ ->
                                    val newPoint = change.position
                                    currentPath?.lineTo(newPoint.x, newPoint.y)
                                    // Trigger recomposition
                                    currentPath = Path().apply {
                                        currentPath?.let { addPath(it) }
                                    }
                                },
                                onDragEnd = {
                                    currentPath?.let {
                                        strokes.add(
                                            DrawnStroke(
                                                path = it,
                                                color = if (isEraser) Slate800 else currentColor,
                                                strokeWidth = if (isEraser) strokeWidth * 3 else strokeWidth,
                                                isHighlighter = isHighlighter
                                            )
                                        )
                                    }
                                    currentPath = null
                                },
                                onDragCancel = {
                                    currentPath = null
                                }
                            )
                        }
                ) {
                    // Optional math grid lines
                    if (showGrid) {
                        val gridSize = 24.dp.toPx()
                        var x = 0f
                        while (x < size.width) {
                            drawLine(
                                color = Color(0x1A94A3B8),
                                start = Offset(x, 0f),
                                end = Offset(x, size.height),
                                strokeWidth = 1f
                            )
                            x += gridSize
                        }
                        var y = 0f
                        while (y < size.height) {
                            drawLine(
                                color = Color(0x1A94A3B8),
                                start = Offset(0f, y),
                                end = Offset(size.width, y),
                                strokeWidth = 1f
                            )
                            y += gridSize
                        }
                    }

                    // Existing strokes
                    strokes.forEach { stroke ->
                        drawPath(
                            path = stroke.path,
                            color = if (stroke.isHighlighter) stroke.color.copy(alpha = 0.35f) else stroke.color,
                            style = Stroke(
                                width = stroke.strokeWidth,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }

                    // Current drawing stroke
                    currentPath?.let { path ->
                        drawPath(
                            path = path,
                            color = if (isEraser) Slate800 else if (isHighlighter) currentColor.copy(alpha = 0.35f) else currentColor,
                            style = Stroke(
                                width = if (isEraser) strokeWidth * 3 else strokeWidth,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tool bar: Modes & Colors & Stroke Width
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Mode Chips
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    AssistChip(
                        onClick = { isEraser = false; isHighlighter = false },
                        label = { Text("Kalem", fontSize = 11.sp) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (!isEraser && !isHighlighter) Color(0xFF4F46E5) else Color.Transparent,
                            labelColor = Color.White
                        ),
                        modifier = Modifier.height(28.dp).testTag("mode_pen")
                    )
                    AssistChip(
                        onClick = { isEraser = false; isHighlighter = true },
                        label = { Text("Fosforlu", fontSize = 11.sp) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (isHighlighter) Color(0xFF0EA5E9) else Color.Transparent,
                            labelColor = Color.White
                        ),
                        modifier = Modifier.height(28.dp).testTag("mode_highlighter")
                    )
                    AssistChip(
                        onClick = { isEraser = true; isHighlighter = false },
                        label = { Text("Silgi", fontSize = 11.sp) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (isEraser) Color(0xFFEF4444) else Color.Transparent,
                            labelColor = Color.White
                        ),
                        modifier = Modifier.height(28.dp).testTag("mode_eraser")
                    )
                }

                // Color picker circles
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    colors.forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (currentColor == color && !isEraser) 2.dp else 0.dp,
                                    color = Color.White,
                                    shape = CircleShape
                                )
                                .clickable {
                                    currentColor = color
                                    isEraser = false
                                }
                        )
                    }
                }
            }
        }
    }
}
