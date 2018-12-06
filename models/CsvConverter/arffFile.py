from game import Game

class ArffFile:
  __attr = []
  __data = []
  def setTitle(self, title):
    self.__title = title

  def addAttribute(self, attr):
    self.__attr.append(attr)

  def addData(self, data):
    self.__data.append(data)

  def outputArff(self, stream):
    stream.write("@RELATION " + self.__title + "\n")
    for attr in self.__attr:
      stream.write("@ATTRIBUTE " + attr + "\n")
    stream.write("@DATA\n")
    for data in self.__data:
      stream.write(data + "\n")
