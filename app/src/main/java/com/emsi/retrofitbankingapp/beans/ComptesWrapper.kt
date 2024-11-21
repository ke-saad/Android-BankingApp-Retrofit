package com.emsi.retrofitbankingapp.beans
import Compte
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "comptes")
data class ComptesWrapper(
    @field:ElementList(entry = "compte", inline = true)
    var comptes: List<Compte> = mutableListOf()
)