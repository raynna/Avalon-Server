package raynna.data.rscm

object RscmResolver {
    fun <T> buildIdMap(
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

    /**
     * Builds a (id, option) → entry map for cases where multiple entries
     * share the same NPC/object id but differ by interaction option
     * (e.g. fishing spots with Net on option 1 and Bait on option 2).
     *
     * [optionSelector] extracts the option number from each entry.
     */
    fun <T> buildIdMapWithOption(
        entries: Iterable<T>,
        refsSelector: (T) -> List<Any>,
        optionSelector: (T) -> Int,
    ): Map<Pair<Int, Int>, T> =
        buildMap {
            for (entry in entries) {
                val option = optionSelector(entry)
                for (ref in refsSelector(entry)) {
                    when (ref) {
                        is Int -> {
                            put(ref to option, entry)
                        }

                        is String -> {
                            val ids =
                                when {
                                    ref.startsWith("npc_group.") -> Rscm.lookupList(ref)
                                    ref.startsWith("npc.") -> listOf(Rscm.lookup(ref))
                                    else -> Rscm.resolve(ref) // objects / fallback
                                }
                            ids.forEach { put(it to option, entry) }
                        }

                        else -> {
                            error("Unsupported reference type: $ref")
                        }
                    }
                }
            }
        }
}
