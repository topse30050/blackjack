from csvFile import CsvFile

inputPath = './training-data.csv'
outputPath = './burst.arff'

file = CsvFile()
file.readCsv(inputPath)
file.convertToBurstModelArff(outputPath)
