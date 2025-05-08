package com.example.codenamesapp


import com.example.codenamesapp.model.Card
import com.example.codenamesapp.model.GameState
import com.example.codenamesapp.model.TeamRole
import com.example.codenamesapp.model.Role

class Communication{
    //give Information from the Server
    //________________________________
    //Spymaster give a hint and the number of hints
    fun giveHint(hint: Array<String>){

    }

    //Operater give a Clue     (-1 ENDTURN)
    fun giveCard(postion: Int){

    }


    //get Information to the Server
    //______________________________

    //get Gamestate, TeamState, Cardlist and Score
    fun getGame(): PayloadResponseMove{
        val exampleGameState = GameState.SPYMASTER_TURN
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