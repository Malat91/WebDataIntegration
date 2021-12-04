import xml.etree.ElementTree as ET
tree = ET.parse('input/target_earthquakes_3_without_duplicates_replaced_countries_time_normalized.xml')
# tree = ET.parse('output/fused.xml')
root = tree.getroot()

i = 0
for child in root:
    i+=1

print (i)
