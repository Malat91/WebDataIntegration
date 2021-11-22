import xml.etree.ElementTree as xml
import xml.dom.minidom as md
from lxml import etree
import reverse_geocode
import collections

class Earthquake:
    def __init__(self):
        self.id = None
        self.date = None
        self.time = None
        self.magnitude = None
        self.depth = None
        self.country = None
        self.latitude = None
        self.longitude = None
        self.deaths = None
        self.totalDamages = None
    
    def __repr__(self):
        return "\n".join([f"{k}:\t{v}" for k,v in self.__dict__.items()])


def parse_file(xml_file):
    """Parses file and removes earthquakes without date"""
    e_list = []
    no_date_counter = 0
    tree = xml.parse(xml_file)
    root = tree.getroot()
    earthquakes = root.getchildren()
    e_ids = []
    for eq in earthquakes:
        e = Earthquake()
        eq_children = eq.getchildren()
        for eq_child in eq_children:
            setattr(e, eq_child.tag, eq_child.text)
        
        e_list.append(e)
        
    return e_list
    
def retrieve_country_from_coord(e):
    noc = False
    try:
        c = reverse_geocode.search([(e.latitude,e.longitude)])[0]["country"]
        if c != e.country:
            r = c
        else:
            r = e.country
    except IndexError:
        r = e.country
        noc = True
    
    return r,noc

def rewrite_countries(e_list):
    no_country_found = 0
    renamed = 0
    matched_countries = collections.defaultdict(list)
    for e in e_list:
        e_old = e.country
        e.country,noc_bool = retrieve_country_from_coord(e)
        
        # count not found countries
        if noc_bool:
            no_country_found += 1
        
        # compare old and new
        if e_old != e.country:
            matched_countries[e_old].append(e.country)
            renamed += 1
    return e_list, matched_countries, no_country_found, renamed

def export_xml(e_list,filename):
    root=xml.Element("earthquakes")
    for e in e_list:
        child = xml.SubElement(root,"earthquake")
        for att_name,att_value in e.__dict__.items():
            
            nm = xml.SubElement(child,att_name)
            nm.text = att_value
                        
    tree = xml.ElementTree(root)
    os = md.parseString(
            xml.tostring(
              tree.getroot(),
              'utf-8')).toprettyxml(indent="\t")
    with open(filename, "w") as g:
        g.write(os)                    
                

if __name__ == "__main__":
    loc = "giddi/WebDataIntegration/identity_resolution/data/input/"
    e_list = parse_file(loc + f"target_earthquakes_3_without_duplicates.xml")
    e_list_new, matched_countries, no_country_found,renamed = rewrite_countries(e_list)
    for k,v in matched_countries.items():
        print(f"{k} -> {set(v)}")
    print(f"no country found: {no_country_found} of {len(e_list)}")
    print(f"renamed earthquakes: {renamed}")
    export_xml(e_list_new,loc+f"target_earthquakes_3_without_duplicates_new.xml")



