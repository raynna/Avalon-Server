package com.rs.kotlin.rscm

object RscmResolver {
    fun <T> buildObjectIdMap(
        entries: Iterable<T>,
        refsSelector: (T) -> List<Any>,
    ): Map<Int, T> =
        buildMap {
            for (entry in entries) {
                for (ref in refsSelector(entry)) {
                    when (ref) {
                        is Int -> {
                            put(ref, entry)
                        }

                        is String -> {
                            Rscm.resolve(ref).forEach {
                                put(it, entry)
                            }
                        }

                        else -> {
                            error("Unsupported reference type: $ref")
                        }
                    }
                }
            }
        }
}
