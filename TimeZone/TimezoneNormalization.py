#pip install timezonefinder
#pip install pytz
import xml.etree.ElementTree as xml
import xml.dom.minidom as md
from lxml import etree


from datetime import datetime, timedelta
from pytz import timezone, utc
from timezonefinder import TimezoneFinder


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

##Timezone offset-----------------------------------------
def get_offset(*, lat, lng):
    """
    returns a location's time zone offset from UTC in hours.
    """

    today = datetime.now()
    tz_target = timezone(tf.certain_timezone_at(lng=lng, lat=lat))
    # ATTENTION: tz_target could be None! handle error case
    today_target = tz_target.localize(today)
    today_utc = utc.localize(today)
    return (today_utc - today_target).total_seconds() / 60 /60
##--------------------------------------------------------


def normalize_time(e_list):
    e_list_updated = []
    changed_counter = 0
    zero_utc_counter = 0
    value_error = 0
    for e in e_list:
        e_new = e
        if (e.time != None):
            dt_concat = e.date + " " + e.time
            dt = datetime.strptime(dt_concat,'%Y-%m-%d %H:%M:%S')
            offset = 0
            if (e.latitude != None and e.longitude != None):
                if (abs(float(e.latitude)) <= 90 and abs(float(e.longitude)) <= 180):#bigger than max long and lat values
                    coordinates = {"lat": float(e.latitude), "lng": float(e.longitude)}
                    offset = get_offset(**coordinates)*-1 #to get from local back to utc we have to take the oposite.
                    if (offset == 0):
                        zero_utc_counter += 1
                    changed_counter += 1
                else:
                    value_error += 1

            dt = dt + timedelta(hours=offset)
            e_new.date = str(dt.date())
            e_new.time = str(dt.time())
        e_list_updated.append(e_new)
    return e_list_updated, zero_utc_counter, changed_counter, value_error


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

    tf = TimezoneFinder()
    bergamo = {"lat": 45.69, "lng": -90.67}
    hours_offset = get_offset(**bergamo)
    print (hours_offset)

    e_list = parse_file(f"TimeZone/target_earthquakes_3_without_duplicates_replaced_countries.xml")
    normalize_tuple = normalize_time(e_list)
    e_list_new = normalize_tuple[0]
    zero_utc_counter = normalize_tuple[1]
    changed_counter = normalize_tuple[2]
    value_error = normalize_tuple[3]
    print (zero_utc_counter)#4
    print (changed_counter)#1051
    print (value_error)#4
    export_xml(e_list_new,f"TimeZone/target_earthquakes_3_without_duplicates_replaced_countries_time_normalized.xml")

    e_list = parse_file(f"TimeZone/target_earthquakes_3_without_duplicates.xml")
    normalize_tuple = normalize_time(e_list)
    e_list_new = normalize_tuple[0]
    zero_utc_counter = normalize_tuple[1]
    changed_counter = normalize_tuple[2]
    value_error = normalize_tuple[3]
    print (zero_utc_counter)
    print (changed_counter)
    print (value_error)
    export_xml(e_list_new,f"TimeZone/target_earthquakes_3_without_duplicates_time_normalized.xml")


    #e_dict = parse_file(f"target_earthquakes_3_without_duplicates.xml")
    #e_dict_new = normalize_time(e_dict)
    #export_xml(e_dict_new,f"target_earthquakes_3_without_duplicates_time_normalized.xml")
