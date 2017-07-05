package com.codeabovelab.tpc.tool.process

import com.codeabovelab.tpc.text.TextCoordinates

data class LabelsEntry(
        val coordinates: TextCoordinates,
        val labels: Collection<LabelEntry>
)