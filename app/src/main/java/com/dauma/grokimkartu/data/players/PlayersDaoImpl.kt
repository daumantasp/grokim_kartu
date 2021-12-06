package com.dauma.grokimkartu.data.players

import com.dauma.grokimkartu.data.players.entities.PlayerDao
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class PlayersDaoImpl(
    private val firebaseFirestore: FirebaseFirestore,
) : PlayersDao {

    companion object {
        private const val playersCollection = "users"
    }

    //https://firebase.google.com/docs/firestore/query-data/query-cursors#kotlin+ktx_3
    // TODO: implement pagination and filter only visible ones?
    //https://medium.com/firebase-tips-tricks/how-to-paginate-firestore-using-paging-3-on-android-c485acb0a2df
    // NOTE: I do not like this very much but because I am using NoSQL database and do not
    // want to duplicate data, I decided this architectural approach:
    // actually do not have players table which would duplicate profiles data,
    // instead use directly profiles data
    // this stuff I am doing in players dao,
    // so later If want I can change with better approach
    override fun getPlayers(onComplete: (Boolean, List<PlayerDao>?, Exception?) -> Unit) {
        firebaseFirestore
            .collection(PlayersDaoImpl.playersCollection)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val players: MutableList<PlayerDao> = mutableListOf()
                for (queryDocumentSnapshot in querySnapshot) {
                    val id = queryDocumentSnapshot.getString("id") ?: ""
                    val visible = queryDocumentSnapshot.getBoolean("visible") ?: false
                    if (visible == true) {
                        var instrument: String = ""
                        var description: String? = null
                        val profileMap = queryDocumentSnapshot.get("profile") as MutableMap<*, *>?
                        if (profileMap != null) {
                            for (profile in profileMap) {
                                if (profile.key == "instrument") {
                                    instrument = profile.value as String
                                } else if (profile.key == "description") {
                                    description = profile.value as String?
                                }
                            }
                        }
                        val playerDao = PlayerDao(
                            id,
                            Timestamp.now(),
                            visible,
                            "",
                            instrument,
                            description
                        )
                        players.add(playerDao)
                    }
                }
                onComplete(true, players, null)
            }
            .addOnFailureListener { e ->
                onComplete(false, null, e)
            }
    }
}