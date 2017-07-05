package com.codeabovelab.tpc.tool.process

data class LabelsData(
        val min: Map<String, Double>,
        val max: Map<String, Double>,
        val entries: List<LabelsEntry>
)