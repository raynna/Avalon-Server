package raynna.data.rscm

sealed class ObjectRef {
    data class Group(
        val name: String,
    ) : ObjectRef()

    data class Single(
        val name: String,
    ) : ObjectRef()

    data class Id(
        val id: Int,
    ) : ObjectRef()
}
