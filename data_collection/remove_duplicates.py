import xml.etree.ElementTree as xml
import xml.dom.minidom as md
from lxml import etree

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
    e_dict = {}
    no_date_counter = 0
    tree = xml.parse(xml_file)
    root = tree.getroot()
    earthquakes = root.getchildren()
    e_ids = []
    for eq in earthquakes:
        e = Earthquake()
        eq_children = eq.getchildren()
        for eq_child in eq_children:
            if eq_child.tag not in ["consequences","location"]:
                setattr(e, eq_child.tag, eq_child.text)
            else:
                for eq_subchild in eq_child:
                    setattr(e, eq_subchild.tag, eq_subchild.text)
        # add earthquake, if is not already added
        if e.id not in e_ids:
            if e.date != None:
                if e.date not in e_dict.keys():
                    e_dict[e.date] = [e]
                else:
                    e_dict[e.date].append(e)
            else:
                no_date_counter += 1
        e_ids.append(e.id)
    print(f"Earthquakes without date: {no_date_counter}")
    return e_dict
    

def subset_or_duplicate(old,new):
    for k,v in old.__dict__.items():
        if k not in ["id","date"]:            
            if k not in new.__dict__.keys():
                #print("old has key that new doesnt")
                return False # old has key that new doesnt
            elif v != None and new.__dict__[k] == None:
                return False # old has non-none values where new has none
            elif v != new.__dict__[k] and v != None:
                return False # old has different (not-none) value from new
    return True

def subset_or_duplicate2(old,new):
    for k,v in old.items():
        if k not in ["id","date"]:            
            if k not in new.keys():
                #print("old has key that new doesnt")
                return False # old has key that new doesnt
            elif v != None and new[k] == None:
                return False # old has non-none values where new has none
            elif v != new[k] and v != None:
                return False # old has different (not-none) value from new
    return True


def drop_duplicates(e_dict):
    deduplicated_e_dict = {}
    dup = 0
    for date, e_list in e_dict.items():
        e_per_day = len(e_list)
        if e_per_day == 1:
            deduplicated_e_dict[date] = e_list
        else:
            for e in e_list:
                if date in deduplicated_e_dict.keys():
                    for e_old in deduplicated_e_dict[date]:
                        is_subset_or_duplicate = subset_or_duplicate(e_old,e)
                        if is_subset_or_duplicate == True:
                            # replace e_old with e
                            deduplicated_e_dict[date].remove(e_old)
                            deduplicated_e_dict[date].append(e)
                            dup += 1
                            print(e_old)
                            print(e)
                            break
                    if is_subset_or_duplicate == False:
                        # in case no subset or duplicate was detected, e is append to list
                        deduplicated_e_dict[date].append(e)
                else:
                    deduplicated_e_dict[date] = [e]
                    
    
    e_before = sum([len(e_list) for e_list in e_dict.values()])
    e_after = sum([len(e_list) for e_list in deduplicated_e_dict.values()])
    print(f"duplicates: {dup}")
    print(f"entries before: {e_before}")
    print(f"entries after: {e_after}")
    print(f"entries after should be: {e_before - dup}") 

    return deduplicated_e_dict

def export_xml(e_dict,filename):
    root=xml.Element("earthquakes")
    e_ids = []
    for date, e_list in e_dict.items():
        for e in e_list:
            loc_exists = False
            con_exists = False
            #location_list = ["latitude","longitude","country"]
            #con_list = ["deaths","totalDamages"]
            child = xml.SubElement(root,"earthquake")
            for att_name,att_value in e.__dict__.items():
                #if att_value != None:
                """if att_name in location_list:
                    if loc_exists == False:
                        category = xml.SubElement(child,"location")
                        for l in location_list:
                            if e.__dict__[l] != None:
                                loc_child = xml.SubElement(category,l)
                                loc_child.text = e.__dict__[l]
                        loc_exists = True
                elif att_name in con_list:
                    if con_exists == False:
                        category2 = xml.SubElement(child,"consequences")
                        for c in con_list:
                            if e.__dict__[c] != None:
                                con_child = xml.SubElement(category2,c)
                                con_child.text = e.__dict__[c]
                        con_exists = True
                else:   """
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
    """d1 = {"name":None,"time":"3"}
    d2 = {"name":None,"time":"3"}
    print(subset_or_duplicate2(d1,d2))

    d3 = {"name":None,"time":"3"}
    d4 = {"name":"Spain","time":"3"}
    print(subset_or_duplicate2(d3,d4))

    d5 = {"name":"Spain","time":"3"}
    d6 = {"name":None,"time":"3"}
    print(subset_or_duplicate2(d5,d6))

    d7 = {"name":"Italy","time":"3"}
    d8 = {"name":"Spain","time":"3"}
    print(subset_or_duplicate2(d7,d8))

    d9 = {"name":"Spain","time":"3"}
    d10 = {"name":"Spain","time":"3"}
    print(subset_or_duplicate2(d9,d10))

    d30 = {"name":None,"time":"3"}
    d40 = {"name":"Spain","time":None}
    print(subset_or_duplicate2(d30,d40))

    d31 = {"name":"Spain",}
    d41 = {"name":"Spain","time":"3"}
    print(subset_or_duplicate2(d31,d41))


    exit()"""
    l = [1,2,3]
    for ds in l:
        e_dict = parse_file(f"target_earthquakes_{ds}.xml")
        e_dict_new = drop_duplicates(e_dict)
        export_xml(e_dict_new,f"target_earthquakes_{ds}_without_duplicates.xml")



