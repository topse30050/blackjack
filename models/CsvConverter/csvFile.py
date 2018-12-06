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
		arffFile.addAttribute("dealer-1 string")
		arffFile.addAttribute("dealer-2 string")
		arffFile.addAttribute("player1-1 string")
		arffFile.addAttribute("player1-2 string")
		arffFile.addAttribute("player2-1 string")
		arffFile.addAttribute("player2-2 string")
		arffFile.addAttribute("player3-1 string")
		arffFile.addAttribute("player3-2 string")
		arffFile.addAttribute("player4-1 string")
		arffFile.addAttribute("player4-2 string")
		arffFile.addAttribute("total numeric")
		arffFile.addAttribute("result {burst, non-burst}")
		for game in self.__games:
			gameData = game.analyzeBurstData()
			for data in gameData:
				arffFile.addData(data)
		with open(output, "w") as outputFile:
			arffFile.outputArff(outputFile)
