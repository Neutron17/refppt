from bs4 import BeautifulSoup
import requests
import json
import os

books = {
    "GEN"	:"Genesis",
    "EXO"	:"Exodus",
    "LEV"	:"Leviticus",
    "NUM"	:"Numbers",
    "DEU"	:"Deuteronomy",
    "JOS"	:"Joshua",
    "JDG"	:"Judges",
    "RUT"	:"Ruth",
    "1SA"	:"1 Samuel",
    "2SA"	:"2 Samuel",
    "1KI"	:"1 Kings",
    "2KI"	:"2 Kings",
    "1CH"	:"1 Chronicles",
    "2CH"	:"2 Chronicles",
    "EZR"	:"Ezra",
    "NEH"	:"Nehemiah",
    "EST"	:"Esther",
    "JOB"	:"Job",
    "PSA"	:"Psalms",
    "PRO"	:"Proverbs",
    "ECC"	:"Ecclesiastes",
    "SOS"	:"Song of Solomon",
    "ISA"	:"Isaiah",
    "JER"	:"Jeremiah",
    "LAM"	:"Lamentations",
    "EZE"	:"Ezekiel",
    "DAN"	:"Daniel",
    "HOS"	:"Hosea",
    "JOE"	:"Joel",
    "AMO"	:"Amos",
    "OBA"	:"Obadiah",
    "JON"	:"Jonah",
    "MIC"	:"Micah",
    "NAH"	:"Nahum",
    "HAB"	:"Habakkuk",
    "ZEP"	:"Zephaniah",
    "HAG"	:"Haggai",
    "ZEC"	:"Zechariah",
    "MAL"	:"Malachi",
    "MAT"	:"Matthew",
    "MAR"	:"Mark",
    "LUK"	:"Luke",
    "JOH"	:"John",
    "ACT"	:"Acts",
    "ROM"	:"Romans",
    "1CO"	:"1 Corinthians",
    "2CO"	:"2 Corinthians",
    "GAL"	:"Galatians",
    "EPH"	:"Ephesians",
    "PHP"	:"Philippians",
    "COL"	:"Colossians",
    "1TH"	:"1 Thessalonians",
    "2TH"	:"2 Thessalonians",
    "1TI"	:"1 Timothy",
    "2TI"	:"2 Timothy",
    "TIT"	:"Titus",
    "PHM"	:"Philemon",
    "HEB"	:"Hebrews",
    "JAM"	:"James",
    "1PE"	:"1 Peter",
    "2PE"	:"2 Peter",
    "1JO"	:"1 John",
    "2JO"	:"2 John",
    "3JO"	:"3 John",
    "JDE"	:"Jude",
    "REV"	:"Revelation"
}
"""
for key in books.keys():
    os.mkdir(f"html/{key}")
    i = 1
    while True:
        print(f"{key} {i}")
        url = f"https://abibliamindenkie.hu/uj/{key}/{str(i)}"
        response = requests.get(
            url,
            headers={
                "Accept":"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
                "Host":"abibliamindenkie.hu",
            }
        )
        if response.status_code != 200:
            print("next")
            break
        else:
            with open(f"html/{key}/{i}.html", 'wb') as file:
                file.write(response.content)
                file.close()
        i+=1
"""
for key in books.keys():
    os.mkdir(f"njson/{key}")
    i = 0
    while True:
        i+=1
        # Load your HTML file (update path as needed)
        try:
            with open(f'json/{key}/{i}.json', 'r', encoding='utf-8') as infile:
                verse_dict = json.load(infile)
        except FileNotFoundError:
            break


        # Convert dictionary to list of objects
        verses_list = [
            {"number": num, "text": text}
            for num, text in verse_dict.items()
        ]

        # Save the new list format
        with open(f'njson/{key}/{i}.json', 'w', encoding='utf-8') as outfile:
            json.dump(verses_list, outfile, ensure_ascii=False, indent=2)

        print(f"Converted {len(verses_list)} verses to '{key}/{i}.json'")

exit(0)

for key in books.keys():
    os.mkdir(f"json/{key}")
    i = 0
    while True:
        i+=1
        # Load your HTML file (update path as needed)
        try:
            with open(f'html/{key}/{i}.html', 'r', encoding='utf-8') as file:
                soup = BeautifulSoup(file, 'html.parser')
        except FileNotFoundError:
            break

        verses = {}

        # Find all verse <p> tags
        for p in soup.find_all('p', class_='verse'):
            # Extract verse number from <a class="verse__number">
            verse_number_tag = p.find('a', class_='verse__number')
            if not verse_number_tag:
                continue  # skip if no verse number

            verse_number = verse_number_tag.get_text(strip=True)

            # Remove all crossreferences and verse number link
            for tag in p.find_all(['span', 'a']):
                tag.decompose()

            # Get remaining text as verse content
            verse_text = p.get_text(strip=True)

            verses[verse_number] = verse_text

        # Write to JSON
        with open(f'json/{key}/{i}.json', 'w', encoding='utf-8') as outfile:
            json.dump(verses, outfile, ensure_ascii=False, indent=2)

        print(f"Extracted {len(verses)} verses to '{key}/{i}.json'")

