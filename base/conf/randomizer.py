import xml.etree.ElementTree as ET
import random

ET.register_namespace('', "http://sndlib.zib.de/network")
tree = ET.parse('nobel-us.xml')
root = tree.getroot()

demandValues = []
#demands are in root[1]
for demand in root[1]:
	demandValues.append(demand[2].text)

random.shuffle(demandValues)

for i in range(len(demandValues)):
	root[1][i][2].text = demandValues[i]

tree.write('out.xml')
