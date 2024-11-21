import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "compte")
data class Compte(
    @field:Element(name = "id")
    var id: Long = 0,

    @field:Element(name = "solde")
    var solde: Double = 0.0,

    @field:Element(name = "dateCreation")
    var dateCreation: String = "",

    @field:Element(name = "type")
    var type: TypeCompte = TypeCompte.COURANT
)
