package com.codeabovelab.tpc.tool.learn

import org.nd4j.jita.conf.CudaEnvironment
import org.nd4j.linalg.factory.Nd4j

object PerfomanceSettings {

    fun useWorkspacesGC() {
        Nd4j.getMemoryManager().autoGcWindow = 10 * 1024 //https://deeplearning4j.org/workspaces
    }

    fun useCuda() {
        CudaEnvironment.getInstance().getConfiguration() //https://deeplearning4j.org/gpu
                .setMaximumGridSize(512)
                .setMaximumBlockSize(512)
                .setMaximumDeviceCacheableLength(1024 * 1024 * 1024L)
                .setMaximumDeviceCache(6L * 1024 * 1024 * 1024L)
                .setMaximumHostCacheableLength(1024 * 1024 * 1024L)
                .setMaximumHostCache(6L * 1024 * 1024 * 1024L)
    }
}