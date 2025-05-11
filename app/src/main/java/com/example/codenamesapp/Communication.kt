package com.example.codenamesapp

import com.example.codenamesapp.model.Card
import com.example.codenamesapp.model.GamePhase
import com.example.codenamesapp.model.TeamRole
import com.example.codenamesapp.model.Role
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

class Communication {

    var latestGameState: PayloadResponseMove? = null
        private set

    //give Information from the Server
    //________________________________
    //Spymaster give a hint and the number of hints
    fun giveHint(hint: Array<String>): String {
        return "HINT:${hint[0]}:${hint[1]}"
    }

    //Operater give a Clue     (-1 ENDTURN)
    fun giveCard(position: Int): String {
        return "SELECT:$position"
    }

    // Prepare game start command
    fun gameStart(): String {
        return "START_GAME"
    }

    // Parse incoming GAME_STATE:{json} from server
    fun updateFromServerMessage(line: String) {
        if (line.startsWith("GAME_STATE:")) {
            val jsonPart = line.removePrefix("GAME_STATE:")
            try {
                val response = Json.decodeFromString<PayloadResponseMove>(jsonPart)
                latestGameState = response
            } catch (e: Exception) {
                println("[Communication] Failed to parse GAME_STATE: ${e.message}")
            }
        }
    }

    fun reset() {
        latestGameState = null
    }

    //get Information to the Server
    //______________________________

    //get Gamestate, TeamState, Cardlist and Score
    fun getGame(): PayloadResponseMove{
        val exampleGameState = GamePhase.SPYMASTER_TURN
        val exampleTeamRole = TeamRole.BLUE
        val exampleCardList = listOf(
            // Rote Karten (9)
            Card("Feuer", Role.RED),
            Card("Blut", Role.RED),
            Card("Rose", Role.RED),
            Card("Apfel", Role.RED),
            Card("Kirsche", Role.RED),
            Card("Erdbeere", Role.RED),
            Card("Tomate", Role.RED),
            Card("Rubin", Role.RED),
            Card("Wein", Role.RED),

            // Blaue Karten (8)
            Card("Wasser", Role.BLUE),
            Card("Himmel", Role.BLUE),
            Card("Ozean", Role.BLUE),
            Card("Saphir", Role.BLUE),
            Card("Eis", Role.BLUE),
            Card("See", Role.BLUE),
            Card("Blume", Role.BLUE),
            Card("Jeans", Role.BLUE),

            // Neutrale Karten (7)
            Card("Tisch", Role.NEUTRAL),
            Card("Stuhl", Role.NEUTRAL),
            Card("Lampe", Role.NEUTRAL),
            Card("Buch", Role.NEUTRAL),
            Card("Haus", Role.NEUTRAL),
            Card("Baum", Role.NEUTRAL),
            Card("Sonne", Role.NEUTRAL),

            // Assassin (1)
            Card("Schatten", Role.ASSASSIN)
        )
        val exampleScoreArray = arrayOf(9, 8)

        val payloadResponseMoveObject = PayloadResponseMove(
            gameState = exampleGameState,
            teamRole = exampleTeamRole,
            card = exampleCardList,
            score = exampleScoreArray
        )
        return payloadResponseMoveObject
    }
}