from playerHand import PlayerHand
from card import Card

class Game:
	def __init__(self, line):
		data = line.split(',')
		self.__dealer = PlayerHand([Card(data[1]), Card(data[2]), Card(data[3]), Card(data[4]), Card(data[5])])
		self.__players = list()
		for i in range(4):
			index = 7 + (i * 7)
			self.__players.append(PlayerHand([Card(data[index]), Card(data[index + 1]), Card(data[index + 2]), Card(data[index + 3]), Card(data[index + 4])]))

	def analyzeBurstData(self):
		data = []
		initialHands = ""
		for card in self.__dealer.getInitialHand():
			initialHands += card.getCard() + ","
		for player in self.__players:
			for card in player.getInitialHand():
				initialHands += card.getCard() + ","
		for player in self.__players:
			cards = player.getAvailableHand()
			if (len(cards) <= 2):
				continue
			for i in range(2, len(cards)):
				hands = []
				for j in range(i):
					hands.append(Card(cards[j]))
				hand = PlayerHand(hands)
				line = initialHands + str(hand.score()) + ","
				if (hand.isBurst()):
					line += "burst"
				else:
					line += "non-burst"
				data.append(line)
		return data