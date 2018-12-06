class PlayerHand:
	def __init__(self, list):
		self.__hands = list

	def isBurst(self):
		if (self.score() > 21):
			return True
		return False

	def score(self):
		sum = 0
		numOfAce = 0
		for card in self.__hands:
			val = card.getCard()
			sum += card.getCardVal()
			if (card.getCard() == "ACE"):
				numOfAce += 1
		for i in range(numOfAce):
			if (sum > 21):
				sum -= 10
		return sum

	def getInitialHand(self):
		return [self.__hands[0], self.__hands[1]]

	def getAvailableHand(self):
		hands = []
		for card in self.__hands:
			if card.getCard() == "":
				break
			hands.append(card.getCard())
		return hands
