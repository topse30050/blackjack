from game import Game
from arffFile import ArffFile

class CsvFile:
	def __init__(self):
		self.__games = list()

	def readCsv(self, input):
		with open(input, "r") as inputFile:
			for line in inputFile:
				self.__games.append(Game(line))

	def convertToBurstModelArff(self, output):
		arffFile = ArffFile()
		arffFile.setTitle("burstmodel")
		arffFile.addAttribute("dealer {ACE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, NONE}")
		arffFile.addAttribute("player1 {ACE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, NONE}")
		arffFile.addAttribute("player2 {ACE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, NONE}")
		arffFile.addAttribute("player3 {ACE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, NONE}")
		arffFile.addAttribute("player4 {ACE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, NONE}")
		arffFile.addAttribute("card-1 {ACE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, NONE}")
		arffFile.addAttribute("card-2 {ACE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, NONE}")
		arffFile.addAttribute("card-3 {ACE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, NONE}")
		arffFile.addAttribute("total NUMERIC")
		arffFile.addAttribute("result {burst, non-burst}")
		for game in self.__games:
			gameData = game.analyzeBurstData()
			for data in gameData:
				arffFile.addData(data)
		with open(output, "w") as outputFile:
			arffFile.outputArff(outputFile)
