class Card:
	valTable = {
		"ACE": 11,
		"TWO": 2,
		"THREE": 3,
		"FOUR": 4,
		"FIVE": 5,
		"SIX": 6,
		"SEVEN": 7,
		"EIGHT": 8,
		"NINE": 9,
		"TEN": 10,
		"JACK": 10,
		"QUEEN": 10,
		"KING": 10,
		"": 0
	}

	def __init__(self, card):
		self.__card = card

	def getCard(self):
		return self.__card

	def getCardVal(self):
		return self.valTable[self.__card]
