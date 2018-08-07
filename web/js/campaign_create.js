var firstInitForm = false;

var regionList = [{"name": "Worldwide"}, {
    "key": "AD",
    "name": "Andorra",
    "type": "country",
    "country_code": "AD",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "AE",
    "name": "United Arab Emirates",
    "type": "country",
    "country_code": "AE",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "AF",
    "name": "Afghanistan",
    "type": "country",
    "country_code": "AF",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "AG",
    "name": "Antigua",
    "type": "country",
    "country_code": "AG",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "AI",
    "name": "Anguilla",
    "type": "country",
    "country_code": "AI",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "AL",
    "name": "Albania",
    "type": "country",
    "country_code": "AL",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "AM",
    "name": "Armenia",
    "type": "country",
    "country_code": "AM",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "AN",
    "name": "Netherlands Antilles",
    "type": "country",
    "country_code": "AN",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "AO",
    "name": "Angola",
    "type": "country",
    "country_code": "AO",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "AQ",
    "name": "Antarctica",
    "type": "country",
    "country_code": "AQ",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "AR",
    "name": "Argentina",
    "type": "country",
    "country_code": "AR",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "AS",
    "name": "American Samoa",
    "type": "country",
    "country_code": "AS",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "AT",
    "name": "Austria",
    "type": "country",
    "country_code": "AT",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "AU",
    "name": "Australia",
    "type": "country",
    "country_code": "AU",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "AW",
    "name": "Aruba",
    "type": "country",
    "country_code": "AW",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "AX",
    "name": "Aland Islands",
    "type": "country",
    "country_code": "AX",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "AZ",
    "name": "Azerbaijan",
    "type": "country",
    "country_code": "AZ",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "BA",
    "name": "Bosnia and Herzegovina",
    "type": "country",
    "country_code": "BA",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "BB",
    "name": "Barbados",
    "type": "country",
    "country_code": "BB",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "BD",
    "name": "Bangladesh",
    "type": "country",
    "country_code": "BD",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "BE",
    "name": "Belgium",
    "type": "country",
    "country_code": "BE",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "BF",
    "name": "Burkina Faso",
    "type": "country",
    "country_code": "BF",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "BG",
    "name": "Bulgaria",
    "type": "country",
    "country_code": "BG",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "BH",
    "name": "Bahrain",
    "type": "country",
    "country_code": "BH",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "BI",
    "name": "Burundi",
    "type": "country",
    "country_code": "BI",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "BJ",
    "name": "Benin",
    "type": "country",
    "country_code": "BJ",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "BL",
    "name": "Saint Barthélemy",
    "type": "country",
    "country_code": "BL",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "BM",
    "name": "Bermuda",
    "type": "country",
    "country_code": "BM",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "BN",
    "name": "Brunei",
    "type": "country",
    "country_code": "BN",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "BO",
    "name": "Bolivia",
    "type": "country",
    "country_code": "BO",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "BQ",
    "name": "Bonaire, Sint Eustatius and Saba",
    "type": "country",
    "country_code": "BQ",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "BR",
    "name": "Brazil",
    "type": "country",
    "country_code": "BR",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "BS",
    "name": "The Bahamas",
    "type": "country",
    "country_code": "BS",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "BT",
    "name": "Bhutan",
    "type": "country",
    "country_code": "BT",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "BV",
    "name": "Bouvet Island",
    "type": "country",
    "country_code": "BV",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "BW",
    "name": "Botswana",
    "type": "country",
    "country_code": "BW",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "BY",
    "name": "Belarus",
    "type": "country",
    "country_code": "BY",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "BZ",
    "name": "Belize",
    "type": "country",
    "country_code": "BZ",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "CA",
    "name": "Canada",
    "type": "country",
    "country_code": "CA",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "CC",
    "name": "Cocos (Keeling) Islands",
    "type": "country",
    "country_code": "CC",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "CD",
    "name": "Democratic Republic of the Congo",
    "type": "country",
    "country_code": "CD",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "CF",
    "name": "Central African Republic",
    "type": "country",
    "country_code": "CF",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "CG",
    "name": "Republic of the Congo",
    "type": "country",
    "country_code": "CG",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "CH",
    "name": "Switzerland",
    "type": "country",
    "country_code": "CH",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "CI",
    "name": "Côte d'Ivoire",
    "type": "country",
    "country_code": "CI",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "CK",
    "name": "Cook Islands",
    "type": "country",
    "country_code": "CK",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "CL",
    "name": "Chile",
    "type": "country",
    "country_code": "CL",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "CM",
    "name": "Cameroon",
    "type": "country",
    "country_code": "CM",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "CN",
    "name": "China",
    "type": "country",
    "country_code": "CN",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "CO",
    "name": "Colombia",
    "type": "country",
    "country_code": "CO",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "CR",
    "name": "Costa Rica",
    "type": "country",
    "country_code": "CR",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "CV",
    "name": "Cape Verde",
    "type": "country",
    "country_code": "CV",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "CW",
    "name": "Curaçao",
    "type": "country",
    "country_code": "CW",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "CX",
    "name": "Christmas Island",
    "type": "country",
    "country_code": "CX",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "CY",
    "name": "Cyprus",
    "type": "country",
    "country_code": "CY",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "CZ",
    "name": "Czech Republic",
    "type": "country",
    "country_code": "CZ",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "DE",
    "name": "Germany",
    "type": "country",
    "country_code": "DE",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "DJ",
    "name": "Djibouti",
    "type": "country",
    "country_code": "DJ",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "DK",
    "name": "Denmark",
    "type": "country",
    "country_code": "DK",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "DM",
    "name": "Dominica",
    "type": "country",
    "country_code": "DM",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "DO",
    "name": "Dominican Republic",
    "type": "country",
    "country_code": "DO",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "DZ",
    "name": "Algeria",
    "type": "country",
    "country_code": "DZ",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "EC",
    "name": "Ecuador",
    "type": "country",
    "country_code": "EC",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "EE",
    "name": "Estonia",
    "type": "country",
    "country_code": "EE",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "EG",
    "name": "Egypt",
    "type": "country",
    "country_code": "EG",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "EH",
    "name": "Western Sahara",
    "type": "country",
    "country_code": "EH",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "ER",
    "name": "Eritrea",
    "type": "country",
    "country_code": "ER",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "ES",
    "name": "Spain",
    "type": "country",
    "country_code": "ES",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "ET",
    "name": "Ethiopia",
    "type": "country",
    "country_code": "ET",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "FI",
    "name": "Finland",
    "type": "country",
    "country_code": "FI",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "FJ",
    "name": "Fiji",
    "type": "country",
    "country_code": "FJ",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "FK",
    "name": "Falkland Islands",
    "type": "country",
    "country_code": "FK",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "FM",
    "name": "Federated States of Micronesia",
    "type": "country",
    "country_code": "FM",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "FO",
    "name": "Faroe Islands",
    "type": "country",
    "country_code": "FO",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "FR",
    "name": "France",
    "type": "country",
    "country_code": "FR",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "GA",
    "name": "Gabon",
    "type": "country",
    "country_code": "GA",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "GB",
    "name": "United Kingdom",
    "type": "country",
    "country_code": "GB",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "GD",
    "name": "Grenada",
    "type": "country",
    "country_code": "GD",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "GE",
    "name": "Georgia",
    "type": "country",
    "country_code": "GE",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "GF",
    "name": "French Guiana",
    "type": "country",
    "country_code": "GF",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "GG",
    "name": "Guernsey",
    "type": "country",
    "country_code": "GG",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "GH",
    "name": "Ghana",
    "type": "country",
    "country_code": "GH",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "GI",
    "name": "Gibraltar",
    "type": "country",
    "country_code": "GI",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "GL",
    "name": "Greenland",
    "type": "country",
    "country_code": "GL",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "GM",
    "name": "The Gambia",
    "type": "country",
    "country_code": "GM",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "GN",
    "name": "Guinea",
    "type": "country",
    "country_code": "GN",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "GP",
    "name": "Guadeloupe",
    "type": "country",
    "country_code": "GP",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "GQ",
    "name": "Equatorial Guinea",
    "type": "country",
    "country_code": "GQ",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "GR",
    "name": "Greece",
    "type": "country",
    "country_code": "GR",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "GS",
    "name": "South Georgia and the South Sandwich Islands",
    "type": "country",
    "country_code": "GS",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "GT",
    "name": "Guatemala",
    "type": "country",
    "country_code": "GT",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "GU",
    "name": "Guam",
    "type": "country",
    "country_code": "GU",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "GW",
    "name": "Guinea-Bissau",
    "type": "country",
    "country_code": "GW",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "GY",
    "name": "Guyana",
    "type": "country",
    "country_code": "GY",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "HK",
    "name": "Hong Kong",
    "type": "country",
    "country_code": "HK",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "HM",
    "name": "Heard Island and McDonald Islands",
    "type": "country",
    "country_code": "HM",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "HN",
    "name": "Honduras",
    "type": "country",
    "country_code": "HN",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "HR",
    "name": "Croatia",
    "type": "country",
    "country_code": "HR",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "HT",
    "name": "Haiti",
    "type": "country",
    "country_code": "HT",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "HU",
    "name": "Hungary",
    "type": "country",
    "country_code": "HU",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "ID",
    "name": "Indonesia",
    "type": "country",
    "country_code": "ID",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "IE",
    "name": "Ireland",
    "type": "country",
    "country_code": "IE",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "IL",
    "name": "Israel",
    "type": "country",
    "country_code": "IL",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "IM",
    "name": "Isle Of Man",
    "type": "country",
    "country_code": "IM",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "IN",
    "name": "India",
    "type": "country",
    "country_code": "IN",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "IO",
    "name": "British Indian Ocean Territory",
    "type": "country",
    "country_code": "IO",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "IQ",
    "name": "Iraq",
    "type": "country",
    "country_code": "IQ",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "IS",
    "name": "Iceland",
    "type": "country",
    "country_code": "IS",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "IT",
    "name": "Italy",
    "type": "country",
    "country_code": "IT",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "JE",
    "name": "Jersey",
    "type": "country",
    "country_code": "JE",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "JM",
    "name": "Jamaica",
    "type": "country",
    "country_code": "JM",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "JO",
    "name": "Jordan",
    "type": "country",
    "country_code": "JO",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "JP",
    "name": "Japan",
    "type": "country",
    "country_code": "JP",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "KE",
    "name": "Kenya",
    "type": "country",
    "country_code": "KE",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "KG",
    "name": "Kyrgyzstan",
    "type": "country",
    "country_code": "KG",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "KH",
    "name": "Cambodia",
    "type": "country",
    "country_code": "KH",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "KI",
    "name": "Kiribati",
    "type": "country",
    "country_code": "KI",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "KM",
    "name": "Comoros",
    "type": "country",
    "country_code": "KM",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "KN",
    "name": "Saint Kitts and Nevis",
    "type": "country",
    "country_code": "KN",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "KP",
    "name": "North Korea",
    "type": "country",
    "country_code": "KP",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "KR",
    "name": "South Korea",
    "type": "country",
    "country_code": "KR",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "KW",
    "name": "Kuwait",
    "type": "country",
    "country_code": "KW",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "KY",
    "name": "Cayman Islands",
    "type": "country",
    "country_code": "KY",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "KZ",
    "name": "Kazakhstan",
    "type": "country",
    "country_code": "KZ",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "LA",
    "name": "Laos",
    "type": "country",
    "country_code": "LA",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "LB",
    "name": "Lebanon",
    "type": "country",
    "country_code": "LB",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "LC",
    "name": "St. Lucia",
    "type": "country",
    "country_code": "LC",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "LI",
    "name": "Liechtenstein",
    "type": "country",
    "country_code": "LI",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "LK",
    "name": "Sri Lanka",
    "type": "country",
    "country_code": "LK",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "LR",
    "name": "Liberia",
    "type": "country",
    "country_code": "LR",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "LS",
    "name": "Lesotho",
    "type": "country",
    "country_code": "LS",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "LT",
    "name": "Lithuania",
    "type": "country",
    "country_code": "LT",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "LU",
    "name": "Luxembourg",
    "type": "country",
    "country_code": "LU",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "LV",
    "name": "Latvia",
    "type": "country",
    "country_code": "LV",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "LY",
    "name": "Libya",
    "type": "country",
    "country_code": "LY",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "MA",
    "name": "Morocco",
    "type": "country",
    "country_code": "MA",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "MC",
    "name": "Monaco",
    "type": "country",
    "country_code": "MC",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "MD",
    "name": "Moldova",
    "type": "country",
    "country_code": "MD",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "ME",
    "name": "Montenegro",
    "type": "country",
    "country_code": "ME",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "MF",
    "name": "Saint Martin",
    "type": "country",
    "country_code": "MF",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "MG",
    "name": "Madagascar",
    "type": "country",
    "country_code": "MG",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "MH",
    "name": "Marshall Islands",
    "type": "country",
    "country_code": "MH",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "MK",
    "name": "Macedonia",
    "type": "country",
    "country_code": "MK",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "ML",
    "name": "Mali",
    "type": "country",
    "country_code": "ML",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "MM",
    "name": "Myanmar",
    "type": "country",
    "country_code": "MM",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "MN",
    "name": "Mongolia",
    "type": "country",
    "country_code": "MN",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "MO",
    "name": "Macau",
    "type": "country",
    "country_code": "MO",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "MP",
    "name": "Northern Mariana Islands",
    "type": "country",
    "country_code": "MP",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "MQ",
    "name": "Martinique",
    "type": "country",
    "country_code": "MQ",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "MR",
    "name": "Mauritania",
    "type": "country",
    "country_code": "MR",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "MS",
    "name": "Montserrat",
    "type": "country",
    "country_code": "MS",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "MT",
    "name": "Malta",
    "type": "country",
    "country_code": "MT",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "MU",
    "name": "Mauritius",
    "type": "country",
    "country_code": "MU",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "MV",
    "name": "Maldives",
    "type": "country",
    "country_code": "MV",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "MW",
    "name": "Malawi",
    "type": "country",
    "country_code": "MW",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "MX",
    "name": "Mexico",
    "type": "country",
    "country_code": "MX",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "MY",
    "name": "Malaysia",
    "type": "country",
    "country_code": "MY",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "MZ",
    "name": "Mozambique",
    "type": "country",
    "country_code": "MZ",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "NA",
    "name": "Namibia",
    "type": "country",
    "country_code": "NA",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "NC",
    "name": "New Caledonia",
    "type": "country",
    "country_code": "NC",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "NE",
    "name": "Niger",
    "type": "country",
    "country_code": "NE",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "NF",
    "name": "Norfolk Island",
    "type": "country",
    "country_code": "NF",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "NG",
    "name": "Nigeria",
    "type": "country",
    "country_code": "NG",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "NI",
    "name": "Nicaragua",
    "type": "country",
    "country_code": "NI",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "NL",
    "name": "Netherlands",
    "type": "country",
    "country_code": "NL",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "NO",
    "name": "Norway",
    "type": "country",
    "country_code": "NO",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "NP",
    "name": "Nepal",
    "type": "country",
    "country_code": "NP",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "NR",
    "name": "Nauru",
    "type": "country",
    "country_code": "NR",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "NU",
    "name": "Niue",
    "type": "country",
    "country_code": "NU",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "NZ",
    "name": "New Zealand",
    "type": "country",
    "country_code": "NZ",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "OM",
    "name": "Oman",
    "type": "country",
    "country_code": "OM",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "PA",
    "name": "Panama",
    "type": "country",
    "country_code": "PA",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "PE",
    "name": "Peru",
    "type": "country",
    "country_code": "PE",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "PF",
    "name": "French Polynesia",
    "type": "country",
    "country_code": "PF",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "PG",
    "name": "Papua New Guinea",
    "type": "country",
    "country_code": "PG",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "PH",
    "name": "Philippines",
    "type": "country",
    "country_code": "PH",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "PK",
    "name": "Pakistan",
    "type": "country",
    "country_code": "PK",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "PL",
    "name": "Poland",
    "type": "country",
    "country_code": "PL",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "PM",
    "name": "Saint Pierre and Miquelon",
    "type": "country",
    "country_code": "PM",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "PN",
    "name": "Pitcairn",
    "type": "country",
    "country_code": "PN",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "PR",
    "name": "Puerto Rico",
    "type": "country",
    "country_code": "PR",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "PS",
    "name": "Palestine",
    "type": "country",
    "country_code": "PS",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "PT",
    "name": "Portugal",
    "type": "country",
    "country_code": "PT",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "PW",
    "name": "Palau",
    "type": "country",
    "country_code": "PW",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "PY",
    "name": "Paraguay",
    "type": "country",
    "country_code": "PY",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "QA",
    "name": "Qatar",
    "type": "country",
    "country_code": "QA",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "RE",
    "name": "Réunion",
    "type": "country",
    "country_code": "RE",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "RO",
    "name": "Romania",
    "type": "country",
    "country_code": "RO",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "RS",
    "name": "Serbia",
    "type": "country",
    "country_code": "RS",
    "supports_region": false,
    "supports_city": true
}, {
    "key": "RU",
    "name": "Russia",
    "type": "country",
    "country_code": "RU",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "RW",
    "name": "Rwanda",
    "type": "country",
    "country_code": "RW",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "SA",
    "name": "Saudi Arabia",
    "type": "country",
    "country_code": "SA",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "SB",
    "name": "Solomon Islands",
    "type": "country",
    "country_code": "SB",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "SC",
    "name": "Seychelles",
    "type": "country",
    "country_code": "SC",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "SE",
    "name": "Sweden",
    "type": "country",
    "country_code": "SE",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "SG",
    "name": "Singapore",
    "type": "country",
    "country_code": "SG",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "SH",
    "name": "Saint Helena",
    "type": "country",
    "country_code": "SH",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "SI",
    "name": "Slovenia",
    "type": "country",
    "country_code": "SI",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "SJ",
    "name": "Svalbard and Jan Mayen",
    "type": "country",
    "country_code": "SJ",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "SK",
    "name": "Slovakia",
    "type": "country",
    "country_code": "SK",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "SL",
    "name": "Sierra Leone",
    "type": "country",
    "country_code": "SL",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "SM",
    "name": "San Marino",
    "type": "country",
    "country_code": "SM",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "SN",
    "name": "Senegal",
    "type": "country",
    "country_code": "SN",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "SO",
    "name": "Somalia",
    "type": "country",
    "country_code": "SO",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "SR",
    "name": "Suriname",
    "type": "country",
    "country_code": "SR",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "SS",
    "name": "South Sudan",
    "type": "country",
    "country_code": "SS",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "ST",
    "name": "Sao Tome and Principe",
    "type": "country",
    "country_code": "ST",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "SV",
    "name": "El Salvador",
    "type": "country",
    "country_code": "SV",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "SX",
    "name": "Sint Maarten",
    "type": "country",
    "country_code": "SX",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "SZ",
    "name": "Swaziland",
    "type": "country",
    "country_code": "SZ",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "TC",
    "name": "Turks and Caicos Islands",
    "type": "country",
    "country_code": "TC",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "TD",
    "name": "Chad",
    "type": "country",
    "country_code": "TD",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "TF",
    "name": "French Southern Territories",
    "type": "country",
    "country_code": "TF",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "TG",
    "name": "Togo",
    "type": "country",
    "country_code": "TG",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "TH",
    "name": "Thailand",
    "type": "country",
    "country_code": "TH",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "TJ",
    "name": "Tajikistan",
    "type": "country",
    "country_code": "TJ",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "TK",
    "name": "Tokelau",
    "type": "country",
    "country_code": "TK",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "TL",
    "name": "Timor-Leste",
    "type": "country",
    "country_code": "TL",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "TM",
    "name": "Turkmenistan",
    "type": "country",
    "country_code": "TM",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "TN",
    "name": "Tunisia",
    "type": "country",
    "country_code": "TN",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "TO",
    "name": "Tonga",
    "type": "country",
    "country_code": "TO",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "TR",
    "name": "Turkey",
    "type": "country",
    "country_code": "TR",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "TT",
    "name": "Trinidad and Tobago",
    "type": "country",
    "country_code": "TT",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "TV",
    "name": "Tuvalu",
    "type": "country",
    "country_code": "TV",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "TW",
    "name": "Taiwan",
    "type": "country",
    "country_code": "TW",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "TZ",
    "name": "Tanzania",
    "type": "country",
    "country_code": "TZ",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "UA",
    "name": "Ukraine",
    "type": "country",
    "country_code": "UA",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "UG",
    "name": "Uganda",
    "type": "country",
    "country_code": "UG",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "UM",
    "name": "United States Minor Outlying Islands",
    "type": "country",
    "country_code": "UM",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "US",
    "name": "United States",
    "type": "country",
    "country_code": "US",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "UY",
    "name": "Uruguay",
    "type": "country",
    "country_code": "UY",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "UZ",
    "name": "Uzbekistan",
    "type": "country",
    "country_code": "UZ",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "VA",
    "name": "Vatican City",
    "type": "country",
    "country_code": "VA",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "VC",
    "name": "Saint Vincent and the Grenadines",
    "type": "country",
    "country_code": "VC",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "VE",
    "name": "Venezuela",
    "type": "country",
    "country_code": "VE",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "VG",
    "name": "British Virgin Islands",
    "type": "country",
    "country_code": "VG",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "VI",
    "name": "US Virgin Islands",
    "type": "country",
    "country_code": "VI",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "VN",
    "name": "Vietnam",
    "type": "country",
    "country_code": "VN",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "VU",
    "name": "Vanuatu",
    "type": "country",
    "country_code": "VU",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "WF",
    "name": "Wallis and Futuna",
    "type": "country",
    "country_code": "WF",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "WS",
    "name": "Samoa",
    "type": "country",
    "country_code": "WS",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "XK",
    "name": "Kosovo",
    "type": "country",
    "country_code": "XK",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "YE",
    "name": "Yemen",
    "type": "country",
    "country_code": "YE",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "YT",
    "name": "Mayotte",
    "type": "country",
    "country_code": "YT",
    "supports_region": false,
    "supports_city": false
}, {
    "key": "ZA",
    "name": "South Africa",
    "type": "country",
    "country_code": "ZA",
    "supports_region": true,
    "supports_city": true
}, {
    "key": "ZM",
    "name": "Zambia",
    "type": "country",
    "country_code": "ZM",
    "supports_region": true,
    "supports_city": false
}, {
    "key": "ZW",
    "name": "Zimbabwe",
    "type": "country",
    "country_code": "ZW",
    "supports_region": true,
    "supports_city": false
}];
var languageList = ["", "Afrikaans", "Albanian", "Arabic", "Armenian", "Azeerbaijani", "Basque", "Belarusian", "Bengali", "Bosnian", "Bulgarian", "Catalan", "Czech", "Cebuano", "Chinese (All)", "Croatian", "Danish", "Dutch", "Dutch (België)", "English (US)", "English (All)", "English (UK)", "Esperanto", "Estonian", "Filipino", "Faroese", "Finnish", "French (All)", "French (Canada)", "French (France)", "Frisian", "Galician", "German", "Georgian", "Greek", "Gujarati", "Guarani", "Hungarian", "Hindi", "Hebrew", "Icelandic", "Indonesian", "Irish", "Italian", "Japanese", "Japanese (Kansai)", "Javanese", "Kannada", "Kazakh", "Khmer", "Korean", "Kurdish (Kurmanji)", "Leet Speak", "Latin", "Latvian", "Lithuanian", "Macedonian", "Malayalam", "Malay", "Marathi", "Mongolian", "Nepali", "Norwegian (bokmal)", "Norwegian (nynorsk)", "Punjabi", "Polish", "Portuguese (All)", "Portuguese (Brazil)", "Portuguese (Portugal)", "Pashto", "Persian", "Romanian", "Russian", "Spanish", "Spanish (Spain)", "Serbian", "Swahili", "Slovak", "Slovenian", "Sinhala", "Spanish (All)", "Simplified Chinese (China)", "Swedish", "Thai", "Turkish", "Traditional Chinese (Hong Kong)", "Traditional Chinese (Taiwan)", "Tamil", "Tajik", "Telugu", "Urdu", "Ukrainian", "Vietnamese", "Welsh"];
var admobLanguageCodes = [{"name": "All", "code": ""}, {"name": "Arabic", "code": 1019}, {
    "name": "Bulgarian",
    "code": 1020
}, {"name": "Catalan", "code": 1038}, {"name": "Chinese (simplified)", "code": 1017}, {
    "name": "Chinese (traditional)",
    "code": 1018
}, {"name": "Croatian", "code": 1039}, {"name": "Czech", "code": 1021}, {
    "name": "Danish",
    "code": 1009
}, {"name": "Dutch", "code": 1010}, {"name": "English", "code": 1000}, {
    "name": "Estonian",
    "code": 1043
}, {"name": "Filipino", "code": 1042}, {"name": "Finnish", "code": 1011}, {
    "name": "French",
    "code": 1002
}, {"name": "German", "code": 1001}, {"name": "Greek", "code": 1022}, {
    "name": "Hebrew",
    "code": 1027
}, {"name": "Hindi", "code": 1023}, {"name": "Hungarian", "code": 1024}, {
    "name": "Icelandic",
    "code": 1026
}, {"name": "Indonesian", "code": 1025}, {"name": "Italian", "code": 1004}, {
    "name": "Japanese",
    "code": 1005
}, {"name": "Korean", "code": 1012}, {"name": "Latvian", "code": 1028}, {
    "name": "Lithuanian",
    "code": 1029
}, {"name": "Malay", "code": 1102}, {"name": "Norwegian", "code": 1013}, {
    "name": "Persian",
    "code": 1064
}, {"name": "Polish", "code": 1030}, {"name": "Portuguese", "code": 1014}, {
    "name": "Romanian",
    "code": 1032
}, {"name": "Russian", "code": 1031}, {"name": "Serbian", "code": 1035}, {
    "name": "Slovak",
    "code": 1033
}, {"name": "Slovenian", "code": 1034}, {"name": "Spanish", "code": 1003}, {
    "name": "Swedish",
    "code": 1015
}, {"name": "Thai", "code": 1044}, {"name": "Turkish", "code": 1037}, {
    "name": "Ukrainian",
    "code": 1036
}, {"name": "Urdu", "code": 1041}, {"name": "Vietnamese", "code": 1040}];
var genderList = ["", "男", "女"];
var osList = ["", "Android_ver_4.0_and_above", "Android_ver_5.0_and_above", "Android_ver_6.0_and_above", "Android_ver_7.0_and_above", "Android_ver_8.0_and_above", "Android_ver_4.0_to_5.0", "Android_ver_5.0_to_6.0", "Android_ver_6.0_to_7.0", "Android_ver_7.0_to_8.0"];
var appList = [];
var admobRegionCodes = {
    "All": "",
    "Sint Maarten": "SX",
    "Cuba": "CU",
    "Curacao": "CW",
    "Iran": "IR",
    "Afghanistan": "AF",
    "Albania": "AL",
    "Antarctica": "AQ",
    "Algeria": "DZ",
    "American Samoa": "AS",
    "Andorra": "AD",
    "Angola": "AO",
    "Antigua and Barbuda": "AG",
    "Azerbaijan": "AZ",
    "Argentina": "AR",
    "Australia": "AU",
    "Austria": "AT",
    "The Bahamas": "BS",
    "Bahrain": "BH",
    "Bangladesh": "BD",
    "Armenia": "AM",
    "Barbados": "BB",
    "Belgium": "BE",
    "Bermuda": "BM",
    "Bhutan": "BT",
    "Bolivia": "BO",
    "Bosnia and Herzegovina": "BA",
    "Botswana": "BW",
    "Bouvet Island": "BV",
    "Brazil": "BR",
    "Belize": "BZ",
    "British Indian Ocean Territory": "IO",
    "Solomon Islands": "SB",
    "British Virgin Islands": "VG",
    "Brunei": "BN",
    "Bulgaria": "BG",
    "Myanmar (Burma)": "MM",
    "Burundi": "BI",
    "Belarus": "BY",
    "Cambodia": "KH",
    "Cameroon": "CM",
    "Canada": "CA",
    "Cape Verde": "CV",
    "Cayman Islands": "KY",
    "Central African Republic": "CF",
    "Sri Lanka": "LK",
    "Chad": "TD",
    "Chile": "CL",
    "China": "CN",
    "Taiwan": "TW",
    "Christmas Island": "CX",
    "Cocos (Keeling) Islands": "CC",
    "Colombia": "CO",
    "Comoros": "KM",
    "Mayotte": "YT",
    "Republic of the Congo": "CG",
    "Democratic Republic of the Congo": "CD",
    "Cook Islands": "CK",
    "Costa Rica": "CR",
    "Croatia": "HR",
    "Cyprus": "CY",
    "Czechia": "CZ",
    "Benin": "BJ",
    "Denmark": "DK",
    "Dominica": "DM",
    "Dominican Republic": "DO",
    "Ecuador": "EC",
    "El Salvador": "SV",
    "Equatorial Guinea": "GQ",
    "Ethiopia": "ET",
    "Eritrea": "ER",
    "Estonia": "EE",
    "Faroe Islands": "FO",
    "Falkland Islands (Islas Malvinas)": "FK",
    "South Georgia and the South Sandwich Islands": "GS",
    "Fiji": "FJ",
    "Finland": "FI",
    "France": "FR",
    "French Guiana": "GF",
    "French Polynesia": "PF",
    "French Southern and Antarctic Lands": "TF",
    "Djibouti": "DJ",
    "Gabon": "GA",
    "Georgia": "GE",
    "The Gambia": "GM",
    "Palestine": "PS",
    "Germany": "DE",
    "Ghana": "GH",
    "Gibraltar": "GI",
    "Kiribati": "KI",
    "Greece": "GR",
    "Greenland": "GL",
    "Grenada": "GD",
    "Guadeloupe": "GP",
    "Guam": "GU",
    "Guatemala": "GT",
    "Guinea": "GN",
    "Guyana": "GY",
    "Haiti": "HT",
    "Heard Island and McDonald Islands": "HM",
    "Vatican City": "VA",
    "Honduras": "HN",
    "Hong Kong": "HK",
    "Hungary": "HU",
    "Iceland": "IS",
    "India": "IN",
    "Indonesia": "ID",
    "Iraq": "IQ",
    "Ireland": "IE",
    "Israel": "IL",
    "Italy": "IT",
    "Cote d'Ivoire": "CI",
    "Jamaica": "JM",
    "Japan": "JP",
    "Kazakhstan": "KZ",
    "Jordan": "JO",
    "Kenya": "KE",
    "South Korea": "KR",
    "Kuwait": "KW",
    "Kyrgyzstan": "KG",
    "Laos": "LA",
    "Lebanon": "LB",
    "Lesotho": "LS",
    "Latvia": "LV",
    "Liberia": "LR",
    "Libya": "LY",
    "Liechtenstein": "LI",
    "Lithuania": "LT",
    "Luxembourg": "LU",
    "Macau": "MO",
    "Madagascar": "MG",
    "Malawi": "MW",
    "Malaysia": "MY",
    "Maldives": "MV",
    "Mali": "ML",
    "Malta": "MT",
    "Martinique": "MQ",
    "Mauritania": "MR",
    "Mauritius": "MU",
    "Mexico": "MX",
    "Monaco": "MC",
    "Mongolia": "MN",
    "Moldova": "MD",
    "Montenegro": "ME",
    "Montserrat": "MS",
    "Morocco": "MA",
    "Mozambique": "MZ",
    "Oman": "OM",
    "Namibia": "NA",
    "Nauru": "NR",
    "Nepal": "NP",
    "Netherlands": "NL",
    "Netherlands Antilles": "BQ",
    "Aruba": "AW",
    "New Caledonia": "NC",
    "Vanuatu": "VU",
    "New Zealand": "NZ",
    "Nicaragua": "NI",
    "Niger": "NE",
    "Nigeria": "NG",
    "Niue": "NU",
    "Norfolk Island": "NF",
    "Norway": "NO",
    "Northern Mariana Islands": "MP",
    "United States Minor Outlying Islands": "UM",
    "Federated States of Micronesia": "FM",
    "Marshall Islands": "MH",
    "Palau": "PW",
    "Pakistan": "PK",
    "Panama": "PA",
    "Papua New Guinea": "PG",
    "Paraguay": "PY",
    "Peru": "PE",
    "Philippines": "PH",
    "Pitcairn Islands": "PN",
    "Poland": "PL",
    "Portugal": "PT",
    "Guinea-Bissau": "GW",
    "Timor-Leste": "TL",
    "Puerto Rico": "PR",
    "Qatar": "QA",
    "Reunion": "RE",
    "Romania": "RO",
    "Russia": "RU",
    "Rwanda": "RW",
    "Saint Helena, Ascension and Tristan da Cunha": "SH",
    "Saint Kitts and Nevis": "KN",
    "Anguilla": "AI",
    "Saint Lucia": "LC",
    "Saint Pierre and Miquelon": "PM",
    "Saint Vincent and the Grenadines": "VC",
    "San Marino": "SM",
    "Sao Tome and Principe": "ST",
    "Saudi Arabia": "SA",
    "Senegal": "SN",
    "Serbia": "RS",
    "Seychelles": "SC",
    "Sierra Leone": "SL",
    "Singapore": "SG",
    "Slovakia": "SK",
    "Vietnam": "VN",
    "Slovenia": "SI",
    "Somalia": "SO",
    "South Africa": "ZA",
    "Zimbabwe": "ZW",
    "Spain": "ES",
    "Western Sahara": "EH",
    "Suriname": "SR",
    "Svalbard and Jan Mayen": "SJ",
    "Swaziland": "SZ",
    "Sweden": "SE",
    "Switzerland": "CH",
    "Tajikistan": "TJ",
    "Thailand": "TH",
    "Togo": "TG",
    "Tokelau": "TK",
    "Tonga": "TO",
    "Trinidad and Tobago": "TT",
    "United Arab Emirates": "AE",
    "Tunisia": "TN",
    "Turkey": "TR",
    "Turkmenistan": "TM",
    "Turks and Caicos Islands": "TC",
    "Tuvalu": "TV",
    "Uganda": "UG",
    "Ukraine": "UA",
    "Macedonia (FYROM)": "MK",
    "Egypt": "EG",
    "United Kingdom": "GB",
    "Guernsey": "GG",
    "Jersey": "JE",
    "Tanzania": "TZ",
    "United States": "US",
    "U.S. Virgin Islands": "VI",
    "Burkina Faso": "BF",
    "Uruguay": "UY",
    "Uzbekistan": "UZ",
    "Venezuela": "VE",
    "Wallis and Futuna": "WF",
    "Samoa": "WS",
    "Yemen": "YE",
    "Zambia": "ZM",
    "Kosovo": "XK"
};

//一种可多选的autocomplete方法封装
function multiSelectAutocomplete(selector, valueList) {
    function split(val) {
        return val.split(/,\s*/);  //逗号及其后跟着的字符串作为分割标志
    }

    function extractLast(term) {
        return split(term).pop();  //分割后的数组返回并删除数组的最后一个元素
    }

    $("#" + selector)
    // 当选择一个条目时不离开文本域
        .bind("keydown", function (event) {
            if (event.keyCode === $.ui.keyCode.TAB &&
                $(this).data("ui-autocomplete").menu.active) {
                event.preventDefault();  //阻止event对象的默认行为发生
            }
        })
        .autocomplete({
            minLength: 0,
            source: function (request, response) {
                // 回到 autocomplete，但是提取最后的条目
                response($.ui.autocomplete.filter(
                    valueList, extractLast(request.term)));
            },
            focus: function () {
                // 防止在获得焦点时插入值
                return false;
            },
            select: function (event, ui) {
                var terms = split(this.value);
                // 移除当前输入
                terms.pop();
                // 添加被选项
                terms.push(ui.item.value);
                // 添加占位符，在结尾添加逗号
                terms.push("");
                this.value = terms.join(",");
                return false;
            }
        });
}

function init() {
    $("#customCountryPartDiv").hide();
    $("#customCountryPartAdmobDiv").hide();
    $('.select2').select2();

    $('#btnCampaignStatus').click(function () {
        popupCenter("campaign_status.jsp", "创建状态监控", 600, 480);
    });

    //“批量输入”: <input type="button">
    $('.btn-more').click(function () {
        var id = $(this).attr('id');
        var targetId = '';
        if (id == 'btnSelectRegionMore') {
            targetId = '#selectRegion';
        } else if (id == 'btnSelectRegionUnselectedMore') {
            targetId = '#selectRegionUnselected';
        } else if (id == 'btnSelectRegionUnselectedAdmobMore') {
            targetId = '#selectRegionUnselectedAdmob';
        } else if (id == 'btnSelectRegionAdmobMore') {
            targetId = '#selectRegionAdmob';
        }
        $('#moreCountryDlg').modal("show");
        $('#moreCountryDlg .btn-primary').off('click');
        $('#moreCountryDlg .btn-primary').click(function () {
            // console.log(id);
            var customCountryPart = $('#inputCustomCountryPart').val().trim();
            var data = $('#textareaCountry').val();
            var countryList = data.split('\n');
            var countryNames = [];
            countryList.forEach(function (one) {
                for (var i = 0; i < regionList.length; i++) {
                    one = one.trim();
                    if (regionList[i].name.toLocaleLowerCase() == one.toLocaleLowerCase()) {
                        if (targetId == '#selectRegionUnselectedAdmob' || targetId == '#selectRegionAdmob') {
                            countryNames.push(regionList[i].country_code);
                        } else {
                            countryNames.push(regionList[i].name);
                        }
                        break;
                    }
                }
            });

            if (targetId === "#selectRegion" || targetId === "#selectRegionAdmob") {
                if (customCountryPart) {
                    if (targetId === "#selectRegion") {
                        $("#customCountryPartDiv").show();
                        $("#customCountryPart").val(customCountryPart).prop("disabled", false);
                    } else {
                        $("#customCountryPartAdmobDiv").show();
                        $("#customCountryPartAdmob").val(customCountryPart).prop("disabled", false);
                    }
                }
                $(targetId).val(countryNames);
                $(targetId).trigger('change');
            }
            $('#moreCountryDlg').modal("hide");
        });
    });

    //以下的六个遍历用于动态添加选项
    languageList.forEach(function (one) {
        $('#selectLanguage').append($("<option>" + one + "</option>"));
    });
    $("#selectLanguage option:first-child").prop("selected", true);

    genderList.forEach(function (one) {
        $('#selectGender').append($("<option>" + one + "</option>"));
    });
    $("#selectGender option:first-child").prop("selected", true);

    osList.forEach(function (one) {
        $('#selectUserOs').append($("<option>" + one + "</option>"));
    });
    $('#selectUserOs option:first-child').prop("selected", true);

    regionList.forEach(function (one) {
        $('#selectRegion').append($("<option>" + one.name + "</option>"));
        $('#selectRegionUnselected').append($("<option>" + one.name + "</option>"));
    });
    $("#selectRegionUnselected").append("<option selected></option>");

    admobLanguageCodes.forEach(function (one) {
        $('#selectLanguageAdmob').append($("<option value='" + one.code + "'>" + one.name + "</option>"));
    });

    for (var k in admobRegionCodes) {
        var key, value;
        key = k;
        value = admobRegionCodes[k];
        $('#selectRegionAdmob').append($("<option value='" + value + "'>" + key + "</option>"));
        $('#selectRegionUnselectedAdmob').append($("<option value='" + value + "'>" + key + "</option>"));
    }
    $('#selectRegionUnselectedAdmob').append("<option selected></option>");

    var pendingList = [1, 2, 3];
    /*
     * 三个 $.post 是异步执行的，哪个先返回response就先执行哪一个的 function
     * 数组 pendingList 的作用在于当最后一个response 返回后执行 initFormData()
     * 而initFormData()是需要三个参数 isAutoCreate, modifyNetwork,modifyRecordId
     */
    $.post('system/fb_app_id_rel/query', {
        word: '',
    }, function (data) {
        if (data && data.ret == 1) {
            appList = data.data;
            appList.forEach(function (one) {
                $('#selectApp').append($("<option>" + one.tag_name + "</option>"));
                $('#selectAppAdmob').append($("<option>" + one.tag_name + "</option>"));
            });
            $("#inputImagePath").val(appList[0].tag_name + "/");
            $("#inputImagePathAdmob").val(appList[0].tag_name + "/");
            pendingList.shift();
            if (pendingList.length == 0) {
                if (isAutoCreate && modifyNetwork != null && modifyRecordId != null) {
                    initFormData();
                }
            }
        } else {
            admanager.showCommonDlg("错误", data.message);
        }
    }, 'json');

    $.post('adaccount_admob/query', {word: ''}, function (data) {
        if (data && data.ret == 1) {
            var accountList = data.data;
            accountList.forEach(function (one) {
                $('#selectAccountAdmob').append($("<option value='" + one.account_id + "'>" + one.short_name + "</option>"));
            });
            pendingList.shift();
            if (pendingList.length == 0) {
                if (isAutoCreate && modifyNetwork != null && modifyRecordId != null) {
                    initFormData();
                }
            }
        } else {
            admanager.showCommonDlg("错误", data.message);
        }
    }, 'json');

    $.post('adaccount/query', {word: ''}, function (data) {
        if (data && data.ret == 1) {
            var accountList = data.data;
            accountList.forEach(function (one) {
                $('#selectAccount').append($("<option value='" + one.account_id + "'>" + one.short_name + "</option>"));
            });
            pendingList.shift();
            if (pendingList.length == 0) {
                if (isAutoCreate && modifyNetwork != null && modifyRecordId != null) {
                    initFormData();
                }
            }
        } else {
            admanager.showCommonDlg("错误", data.message);
        }
    }, 'json');

    $("#selectPublisherPlatforms").html("<option value='facebook'>facebook</option><option value='audience_network'>audience_network</option>" +
        "<option value='messenger'>messenger</option>");

    $('#formAdmob').hide();

}

//执行初始化的方法
init();

/*
 * 该方法用于campaigns_auto_create.jsp与country_analysis_report.jsp页面传来 isAutoCreate 参数时执行
 * 在isAutoCreate情况下，需要 modifyNetwork以及 modifyRecordId
 * 这三个参数是由 campaigns_auto_create.jsp页面传来，用于自动填写选项
 * 此方法仅在campaign_create.js 的 function init(){}内的三个post中调用
 */
function initFormData() {
    if (isAutoCreate) {
        $.post('auto_create_campaign/' + modifyNetwork + '/query_by_id', {
            id: modifyRecordId
        }, function (data) {
            if (data && data.ret == 1) {
                console.log(data);
                if (modifyNetwork == 'facebook') {
                    $('#checkFacebook').prop('checked', true);
                    $('#checkFacebook').click();
                    var campaignData = data.data;
                    var accountIds = campaignData.account_id.split(",");
                    $('#selectApp').val(campaignData.app_name);
                    $("#selectApp").trigger("change");//确保路径能正确导入
                    $('#selectAccount').val(accountIds);
                    $('#selectAccount').trigger('change');
                    $("#inputCreateCount").val(campaignData.create_count);
                    $('#selectRegion').val(campaignData.country_region.split(','));
                    $('#selectRegion').trigger('change');
                    $('#selectRegionExplode').prop('checked', campaignData.explode_country == 1);
                    $('#selectRegionUnselected').val(campaignData.excluded_region.split(','));
                    $('#selectRegionUnselected').trigger('change');
                    $('#selectLanguage').val(campaignData.language);
                    $('#inputAge').val(campaignData.age);
                    $('#inputAgeExplode').prop('checked', campaignData.explode_age == 1);
                    $('#selectGender').val(campaignData.gender.split(','));
                    $('#selectGender').trigger('change');
                    $('#selectGenderExplode').prop('checked', campaignData.explode_gender == 1);
                    $('#inputInterest').val(campaignData.detail_target);
                    $('#selectUserOs').val(campaignData.user_os.split(','));
                    $('#selectUserOs').trigger('change');
                    $('#inputUserDevices').val(campaignData.user_devices);
                    $('#inputBudget').val(campaignData.bugdet);
                    $('#selectBidStrategy').val(campaignData.bidStrategy);
                    $('#inputBidding').val(campaignData.bidding);
                    $('#inputBiddingExplode').prop('checked', campaignData.explode_bidding == 1);
                    $('#inputMaxCpa').val(campaignData.max_cpa);
                    $('#inputTitle').val(campaignData.title);
                    $('#inputMessage').val(campaignData.message);
                    //填入相对路径
                    var imageTrimed = campaignData.image_path.replace(/home\/\w+\/\w+\/\w+\//, "");
                    $('#inputImagePath').val(imageTrimed);
                    var videoTrimed = campaignData.video_path.replace(/home\/\w+\/\w+\/\w+\//, "");
                    $("#inputVideoPath").val(videoTrimed);

                    $('#btnCreate').val('更新');
                    $(".form-check-input").prop("checked", false);
                    $(".form-check-input").prop("disabled", true);
                    $("#onlyCheckAutoCreate").prop("checked", true);
                    $("#onlyCheckAutoCreate").prop("disabled", true);
                    $("#checkAutoCreate").prop("disabled", true);
                } else {
                    $('#checkAdmob').prop('checked', true);
                    $('#checkAdmob').click();
                    var campaignData = data.data;
                    var accountIds = campaignData.account_id.split(",");
                    $('#selectAppAdmob').val(campaignData.app_name);
                    $("#selectAppAdmob").trigger("change"); //确保路径正确导入
                    $('#selectAccountAdmob').val(accountIds);
                    $('#selectAccountAdmob').trigger('change');
                    $("#inputCreateCountAdmob").val(campaignData.create_count);
                    $('#selectRegionAdmob').val(campaignData.country_region.split(','));
                    $('#selectRegionAdmob').trigger('change');
                    $('#selectRegionAdmobExplode').prop('checked', campaignData.explode_country == 1);
                    $('#selectRegionUnselectedAdmob').val(campaignData.excluded_region.split(','));
                    $('#selectRegionUnselectedAdmob').trigger('change');
                    $('#selectLanguageAdmob').val(campaignData.language);
                    $('#inputBudgetAdmob').val(campaignData.bugdet);
                    $('#inputBiddingAdmob').val(campaignData.bidding);
                    $('#inputBiddingAdmobExplode').prop('checked', campaignData.explode_bidding == 1);
                    $('#inputMaxCpaAdmob').val(campaignData.max_cpa);
                    $('#inputMessage1').val(campaignData.message1);
                    $('#inputMessage2').val(campaignData.message2);
                    $('#inputMessage3').val(campaignData.message3);
                    $('#inputMessage4').val(campaignData.message4);

                    var imageTrimed = campaignData.image_path.replace(/home\/\w+\/\w+\/\w+\//, "");
                    $('#inputImagePathAdmob').val(imageTrimed);

                    $('#btnCreateAdmob').val('更新');
                    $(".form-check-input").prop("checked", false);
                    $(".form-check-input").prop("disabled", true);
                    $("#onlyCheckAdmobAutoCreate").prop("checked", true);
                    $("#onlyCheckAdmobAutoCreate").prop("disabled", true);
                    $("#checkAdmobAutoCreate").prop("disabled", true);
                }
            }
        }, "json");
    }
}

//从（暂定）index2.jsp 或 index.jsp传来的参数进行各表单的自动填充
function indexInitFormData(isIndexCreate, campaign_id) {
    if (isIndexCreate) {
        $.post("IndexCampaignCreate", {
            campaign_id: campaign_id
        }, function (data) {
            var str = data;   //这里传回的data是一个字符串，首先要转成一个json
            var campaignData = JSON.parse(str);   //将字符串转成json
            if (campaignData.no_data === "no_data") {
                alert("There's no data which campaign_id = " + campaign_id);
            } else {
                if (campaignData.flag == "facebook") {
                    $('#checkFacebook').prop('checked', true);
                    $('#checkAutoCreate').prop('checked', true);

                    $('#selectApp').val(campaignData.app_name);

                    $('#selectAccount').val(campaignData.account_id);
                    $('#selectAccount').trigger('change');

                    $("#inputCreateCount").val(1);

                    $('#selectRegion').val(campaignData.country_region.split(',')); //这里的val()方法用于设置多个值
                    $('#selectRegion').trigger('change');

                    $('#inputBudget').val(IndexBudget);
                    $('#selectBidStrategy').val(IndexBudget);

                    $('#inputBidding').val(IndexBidding);
                    $('#inputBiddingExplode').prop('checked', campaignData.explode_bidding == 1);

                    $("#selectGender").val(campaignData.gender.split(','));
                    $("#selectGender").trigger('change');
                    $("#inputAge").val(campaignData.age);
                    $("#selectApp").trigger("change");

                } else if (campaignData.flag == "admob") {
                    $('#checkAdmob').prop('checked', true);
                    $('#checkAdmob').click();
                    $('#checkAdmobAutoCreate').prop('checked', true);

                    $('#selectAppAdmob').val(campaignData.app_name);
                    $('#selectAccountAdmob').val(campaignData.account_id);
                    $('#selectAccountAdmob').trigger('change');

                    $("#inputCreateCountAdmob").val(1);

                    $('#inputBudgetAdmob').val(IndexBudget);
                    $('#inputBiddingAdmob').val(IndexBidding);
                    $('#inputBiddingAdmobExplode').prop('checked', campaignData.explode_bidding == 1); //"分离到系列"
                    $("#selectAppAdmob").trigger("change");
                    if (campaignData.country_region != null && campaignData.country_region != "") {
                        $('#selectRegionAdmob').val(campaignData.country_region.split(',')); //将字符串从指定符号处分割为字符串数组
                        $('#selectRegionAdmob').trigger('change');
                    }

                    if (campaignData.excluded_region != "" && campaignData.excluded_region != null) {
                        $('#selectRegionUnselectedAdmob').val(campaignData.excluded_region.split(','));
                        $('#selectRegionUnselectedAdmob').trigger('change');
                    }
                }
            }
        });
    }
}

if (isIndexCreate && campaign_id) {
    indexInitFormData(isIndexCreate, campaign_id);
}

/**
 * 把一个参数数组，根据 explodeParam 交叉合并得到新的参数数组
 * @param params [{x:1,y:2},{x:1,y:3}]
 * @param explodeParam {key:z, values:[4,5,6]}
 * @return Array [{x:1,y:2,z:4}, {x:1,y:2,z:5}, {x:1,y:2,z:6},{x:1,y:3,z:4},{x:1,y:3,z:5},{x:1,y:3,z:6}]
 **/
function getExplodeParams(params, explodeParam) {    //这里的参数由reduce()传入，params是 accumulator，explodeParam是reduce()对象数组当前值
    if (params.length === 0 && explodeParam.values.length > 0) {
        params.push({})
    }
    var createdParams = [];
    params.forEach(function (p) {   // p 是调用对象的一个数组元素
        explodeParam.values.forEach(function (p2) {   // explodeParam.values 是explodeParam里名为"values"的键对应的值，p2为值数组的元素
            var np = $.extend({}, p); //clone
            np[explodeParam.key] = p2; //增加新的键值对
            createdParams.push(np);
        })
    });
    return createdParams;
}

/**
 * @param {{region: string, gender: string, age: string, bidding: string}}
 *
 **/
//以下在Facebook表单的"广告系列名称"中拼凑 系列名称 字符串
function generateFacebookCampaignName(params) {
    // var campaignName = [];
    var publisherPlatformKeyValue = [{"name": "facebook", "code": "FB"}, {
        "name": "audience_network",
        "code": "AN"
    }, {"name": "messenger", "code": "MS"}];
    if (!params) {
        params = {};
    }
    var dims = [];
    var appName = $('#selectApp').val();
    dims.push(appName);
    if (params.fbPageName) {
        dims.push(params.fbPageName);
    }
    var publisherPlatformsValue = $("#selectPublisherPlatforms").val();
    if (params.publisherPlatforms) {
        var pp = params.publisherPlatforms.split(",");
        var code = [];
        pp.forEach(function (cur) {
            publisherPlatformKeyValue.forEach(function (current) {
                if (current.name == cur) {
                    code.push(current.code);
                }
            });
        });
        dims.push(code.join(","));
    } else {
        if (publisherPlatformsValue.length > 0) {
            var code = [];
            publisherPlatformsValue.forEach(function (cur) {
                publisherPlatformKeyValue.forEach(function (current) {
                    if (current.name == cur) {
                        code.push(current.code);
                    }
                });
            });
            dims.push(code.join(","));
        }
    }
    if (params.groupId) {
        dims.push("Group" + params.groupId)
    }
    var region = $('#selectRegion').val();
    var countryAlisa = $('#customCountryPart').val();
    if (countryAlisa) {
        dims.push(countryAlisa);
    } else {
        if (params.region) {
            dims.push(params.region);
        } else {
            dims.push(region.join(","));
        }
    }
    var gender = $('#selectGender').val();
    if (typeof params.gender !== 'undefined') {
        dims.push(params.gender);
    } else {
        dims.push(gender);
    }
    if (params.age) {
        dims.push(params.age);
    } else {
        dims.push($('#inputAge').val());
    }

    if (params.userDevice) {
        dims.push(params.userDevice);
    } else {
        var userDevice = $('#inputUserDevices').val();
        dims.push(userDevice);
    }

    if (params.userOs) {
        dims.push(params.userOs);
    } else {
        var userOs = $('#selectUserOs').val();
        dims.push(userOs);
    }
    var language = $('#selectLanguage').val();
    dims.push(language);

    var accountName = $('#selectAccount option:selected').text();
    dims.push(accountName);

    if (params.identification) {
        if (params.identification == "image") {
            var imagePath = params.materialPath;
            dims.push(imagePath);
        } else if (params.identification == "video") {
            var videoPath = "视频" + params.materialPath;
            dims.push(videoPath);
        }
    } else {
        //这是一个在 回显过程中只显示图片路径的
        var imagePath = $("#inputImagePath").val().trim().replace(/,$/, "");
        dims.push(imagePath);
    }
    return dims.join("_");
}

//以下拼凑 admob系列名称 字符串
function generateAdmobCampaignName(params) {
    if (!params) {
        params = {};
    }
    var dims = [];
    var now = new Date();
    dims.push($('#selectAppAdmob').val());
    if (params.groupId) {
        dims.push("Group" + params.groupId)
    }
    var region = $('#selectRegionAdmob option:selected').text();
    var countryAlisa = $('#customCountryPartAdmob').val();
    if (countryAlisa) {
        dims.push(countryAlisa);
    } else {
        if (params.region) {
            if (params.region.includes(",")) {
                var regionList = params.region.split(",");
                regionList.forEach(function (r, idx) {
                    for (var code in admobRegionCodes) {
                        if (admobRegionCodes[code] === r) {
                            regionList[idx] = code;
                            break;
                        }
                    }
                });
                dims.push(regionList.join(","));
            } else {
                var country = new String();
                for (var code in admobRegionCodes) {
                    if (admobRegionCodes[code] === params.region) {
                        country = code;
                        break;
                    }
                }
                dims.push(country);
            }
        } else {
            dims.push(region);
        }
    }

    dims.push($('#selectLanguageAdmob option:selected').text());

    var curr_event = $('#selectIncidentAdmob option:selected').text();
    if (curr_event != "null" && curr_event != "") {
        dims.push("event_" + $('#selectIncidentAdmob option:selected').text());
    }

    if (params.bidding) {
        dims.push(params.bidding);
    } else {
        dims.push($('#inputBiddingAdmob').val());
    }

    if (params.imagePath) {
        dims.push(params.imagePath);
    } else {
        dims.push($('#inputImagePathAdmob').val().trim().replace(/,$/, ""));
    }

    dims.push(now.getFullYear() + "" + (now.getMonth() + 1) + "" + now.getDate());

    return dims.join("_");
}

//以下两项决定隐藏哪个表单
$('#checkAdmob').click(function () {
    if ($('#checkAdmob').prop('checked')) {
        $('#formFacebook').hide();
        $('#formAdmob').show();
    }
});
$('#checkFacebook').click(function () {
    if ($('#checkFacebook').prop('checked')) {
        $('#formFacebook').show();
        $('#formAdmob').hide();
    }
});

/**
 * @param Array params 一个数组，存放所有的等待请求的参数,这里即
 * @param Function send 处理每一个参数的请求
 * @param Function onFinish 队列全部处理完成后，调用一下
 **/
function batchRequest(params, send, onFinish) {
    var idx = -1;
    var errLog = [];//[{param:object, errMsg:string}]
    var warning = [];
    var stop = false

    function getProgress() {
        return idx + " / " + params.length; //根据JS的规则这里会拼成一个字符串
    }

    function getFullLog() {
        var logs = ["统计 " + getProgress()];
        errLog.forEach(function (log) {
            logs.push(log.errMsg + " : " + JSON.stringify(log.param));
        });
        return logs.join("\n");
    }

    //后台失败信息：（出价||预算||广告||国家||创建数量||性别）不能为空，或 图片/视频路径不存在
    function next() {
        if (stop) {
            admanager.showCommonDlg("终止", getProgress());
            return;
        }
        setTimeout(function () {   //定时任务，直到 idx === param.length的时候 定时任务才结束
            idx++;
            if (idx === params.length) {
                if (errLog.length > 0) {
                    admanager.showCommonDlg("完成", getFullLog());
                } else {
                    setTimeout(function () {
                        $('#common_message_dialog').modal('hide');
                    }, 1500);
                    admanager.showCommonDlg("完成", getFullLog(), function () {
                        onFinish(errLog, warning);
                    });
                }
                return;
            }
            request();
        }, 50);
    }

    function request() {
        send(params[idx], function (message) {
            //本function()是batchRequest()在 send 参数部分传入的onSuccess()函数对象具体执行部分
            if (message != null) {
                warning.push(message);
            }
            next();
        }, function (errMsg) {
            //请求一个失败，要不要重试？
            errLog.push({param: params[idx], errMsg: errMsg});
            // console.log(errLog[errLog.length - 1]);
            next();
        })
    }

    next();
}

//获取facebook主页信息下拉框
function getFBPages(tagName) {
    $.post('system/fb_app_id_rel/queryFBPage', {tagName: tagName}, function (data) {
        if (data) {
            $('#selectFBPage').empty();
            var fbPages = $.parseJSON(data.data);
            var page;
            fbPages.forEach(function (item) {
                page = item.data;
                $('#selectFBPage').append($("<option value='" + page.page_id + "'>" + page.page_name + "</option>"));
            });
        } else {
            admanager.showCommonDlg("错误", data.message);
        }
    }, 'json');
}

//执行由app_name自动补充图片和视频路径
$("#selectApp").change(function () {
    $("#tbody_facebook").empty();
    $("#inputImagePath").val("");
    $("#inputVideoPath").val("");
    $('#checkFacebook').click();
    var checkFacebook = $("#checkFacebook").prop("checked");
    if (checkFacebook) {
        var appName = $("#selectApp").val();
        getFBPages(appName);//获取facebook主页信息下拉框
        $.post("app_image_video_rel/query_facebook_path_by_app", {
            app_name: appName
        }, function (data) {
            if (data.ret === 1) {
                var image_path = [];
                if (data.image_array != null && data.image_array.length > 0) {
                    for (var i = 0; i < data.image_array.length; i++) {
                        var img = data.image_array[i];
                        var imgTrimed = img["image_path"].replace(/home\/\w+\/\w+\/\w+\//, "");
                        imgTrimed = imgTrimed.replace(/(\/.+\/)(.*\.\w+)/, "$1");
                        image_path[i] = imgTrimed;
                    }
                    multiSelectAutocomplete("inputImagePath", image_path);
                    $("#inputImagePath").val(image_path[0]);
                }
                var video_path = [];
                if (data.video_array != null && data.video_array.length > 0) {
                    for (var i = 0; i < data.video_array.length; i++) {
                        var vdo = data.video_array[i];
                        var vdoTrimed = vdo["video_path"].replace(/home\/\w+\/\w+\/\w+\//, "");
                        vdoTrimed = vdoTrimed.replace(/(\/.+\/)(.*\.\w+)/, "$1");
                        video_path[i] = vdoTrimed;
                    }
                    multiSelectAutocomplete("inputVideoPath", video_path);
                    $("#inputVideoPath").val(video_path[0]);
                }
            }
        }, "json");
    }
    var appName = $('#selectApp').val();
    $.post("campaign/selectMaxBiddingByAppName", {
        appName: appName
    }, function (data) {
        if (data && data.ret == 1) {
            $('#inputBidding')[0].placeholder = "最大出价：" + data.max_bidding;
        } else {
            $('#inputBidding')[0].placeholder = "还未设置最大出价";
        }
    }, "json");
    return false;
});
$("#selectAppAdmob").change(function () {
    $("#inputImagePath").val("");
    $('#checkAdmob').click();
    var checkAdmob = $("#checkAdmob").prop("checked");
    if (checkAdmob) {
        var appName = $("#selectAppAdmob").val();
        $.post("app_image_video_rel/query_admob_path_by_app",
            {app_name: appName},
            function (data) {
                var image_path = [];
                if (data.image_array.length > 0) {
                    for (var i = 0; i < data.image_array.length; i++) {
                        var img = data.image_array[i];
                        var imgTrimed = img["image_path"].replace(/home\/\w+\/\w+\/\w+\//, "");
                        imgTrimed = imgTrimed.replace(/(\/.+\/)(.*\.\w+)/, "$1");
                        image_path[i] = imgTrimed;
                    }
                    multiSelectAutocomplete("inputImagePathAdmob", image_path);
                    $("#inputImagePathAdmob").val(image_path[0]);
                }
            }, "json");
    }
    $("#tbody_admob").empty();
    var appNameAdmob = $('#selectAppAdmob').val();
    $.post('advert_conversion_admob/query_advert_conversion_by_app_name', {appName: appNameAdmob}, function (result) {
        if (result && result.ret == 1) {
            var incidentList = result.data;
            $('#selectIncidentAdmob option').remove();
            $('#selectIncidentAdmob').append($("<option value=''>null</option>"));
            incidentList.forEach(function (one) {
                $('#selectIncidentAdmob').append($("<option value='" + one.conversion_id + "'>" + one.conversion_name + "</option>"));
            });
        } else {
            admanager.showCommonDlg("错误", data.message);
        }
    }, 'json');
    $.post("campaign_admob/selectMaxBiddingByAppName", {
        appName: appNameAdmob
    }, function (data) {
        if (data && data.ret == 1) {
            $('#inputBiddingAdmob')[0].placeholder = "最大出价：" + data.max_bidding;
        } else {
            $('#inputBiddingAdmob')[0].placeholder = "还未设置最大出价";
        }
    }, "json");
    return false;
});

var countryBidding = [];
//根据[国家地区][应用名称]回显已创建好的广告语
$("#selectRegion").change(function () {
    $("#appCountryBidding").text("");
    // if (isAutoCreate && !firstInitForm) {
    //     firstInitForm = true;
    //     return;
    // }
    $("#tbody_facebook").empty();
    var region = $('#selectRegion').val();
    if (region != null && region.length > 0) {
        var appName = $('#selectApp').val();
        if (appName != "") {
            //查询出国家出价上限
            $.post("tagsBidAdmanager/selectByTagNameRegion", {
                appName: appName,
                region: region.join(",")
            }, function (data) {
                if (data) {
                    var list = data.data;
                    countryBidding = list;
                    list.forEach(function (one) {
                        // alert(one.country);
                        // alert(one.bidding);
                        $("#appCountryBidding").append("<span>" + one.country + "  :  " + one.bidding + "</span>&nbsp;&nbsp;&nbsp;&nbsp;");

                    });
                } else if (data) {
                    admanager.showCommonDlg("Warning", data.message);
                }
            }, "json");


            $.post("campaign_create_ads_show_up/facebook", {
                appName: appName,
                region: region.join(",")
            }, function (data) {
                if (data && data.ret == 1) {
                    var ads = data.ads;
                    var tbody = $("#advertisement").children("tbody");
                    ads.forEach(function (ad) {
                        var tr = $("<tr></tr>");
                        tr.append("<input type='checkbox' class='check_group'>");
                        var field = ["group_id", "language", "title", "message"];
                        for (var i = 0; i < 4; i++) {
                            var td = $("<td></td>");
                            var value = ad[field[i]];
                            td.text(value);
                            tr.append(td);
                        }
                        tbody.append(tr);
                    });
                } else if (data && data.ret == 0) {
                    admanager.showCommonDlg("Warning", data.message);
                }
                $("#checkbox_facebook").prop("checked", false);
                $("#checkbox_facebook").click();
            }, "json");
        }
    }
    return false;
});

var countryBiddingAdmob = [];
$("#selectRegionAdmob").change(function () {
    $("#appCountryBiddingAdWords").text("");
    // if (isAutoCreate && !firstInitForm) {
    //     firstInitForm = true;
    //     return;
    // }
    $("#tbody_admob").empty();
    var selectOptions = $('#selectRegionAdmob option:selected');
    var regionAdmob = [];
    selectOptions.each(function () {
        regionAdmob.push($(this).text())
    });
    if (regionAdmob != null && regionAdmob.length > 0) {
        var appNameAdmob = $('#selectAppAdmob').val();
        //查询出国家出价上限
        $.post("tagsBidAdmanager/selectByTagNameRegion", {
            appName: appNameAdmob,
            region: regionAdmob.join(",")
        }, function (data) {
            if (data) {
                var list = data.data;
                countryBiddingAdmob = list;
                list.forEach(function (one) {
                    // alert(one.country);
                    // alert(one.bidding);
                    $("#appCountryBiddingAdWords").append("<span>" + one.country + "  :  " + one.bidding + "</span>&nbsp;&nbsp;&nbsp;&nbsp;");

                });
            } else if (data) {
                admanager.showCommonDlg("Warning", data.message);
            }
        }, "json");

        $.post("campaign_create_ads_show_up/adwords", {
            appName: appNameAdmob,
            region: regionAdmob.join(","),
        }, function (data) {
            if (data && data.ret == 1) {
                var ads = data.ads;
                var tbody = $("#advertisement_admob").children("tbody");
                ads.forEach(function (ad) {
                    var tr = $("<tr></tr>");
                    tr.append("<input type='checkbox' class='check_group_admob'>");
                    var field = ["group_id", "language", "message1", "message2", "message3", "message4"];
                    for (var i = 0; i < 6; i++) {
                        var td = $("<td></td>");
                        var value = ad[field[i]];
                        td.text(value);
                        tr.append(td);
                    }
                    tbody.append(tr);
                });
            } else if (data && data.ret == 0) {
                admanager.showCommonDlg("Warning", data.message);
            }
            $("#checkbox_admob").prop("checked", false);
            $("#checkbox_admob").click();
        }, "json");
    }
    return false;
});

//两个表单广告语的全选
$("#checkbox_facebook").click(function () {
    if ($("#checkbox_facebook").prop("checked")) {
        $(".check_group").prop("checked", true);
    } else {
        $(".check_group").prop("checked", false);
    }
});
$("#checkbox_admob").click(function () {
    if ($("#checkbox_admob").prop("checked")) {
        $(".check_group_admob").prop("checked", true);
    } else {
        $(".check_group_admob").prop("checked", false);
    }
});

//读取并分离Facebook表单数据
function FacebookFormReading() {
    var appName = $('#selectApp').val();
    var selectOptions = $('#selectAccount option:selected');
    var accountName = [];
    var accountId = [];
    selectOptions.each(function () {
        accountName.push($(this).text());
        accountId.push($(this).val());
    });

    var createCount = $("#inputCreateCount").val();
    var region = $('#selectRegion').val();
    var fbPage = $('#selectFBPage').val();//facebooke主页
    //facebook主页名称
    var fbName = $("#selectFBPage").children("option:selected"); //这里仅用于拼写
    var fbPageName = [];
    fbName.each(function (idx) {
        fbPageName.push($(this).text());
    });


    var excludedRegion = $('#selectRegionUnselected').val();
    var language = $('#selectLanguage').val();
    var age = $('#inputAge').val();
    var gender = $('#selectGender').val();
    var interest = $('#inputInterest').val();
    var userOs = $('#selectUserOs').val();
    var userDevice = $('#inputUserDevices').val();
    var bugdet = $('#inputBudget').val();//预算
    /****************************************************************************/
    var flag = "";
    if ($('#flag').prop('checked')) {
        flag = "1";
    }

    var bidding = $('#inputBidding').val();//出价/竞价

    var bidStrategy = $('#selectBidStrategy').val();//竞价策略

    var maxCPA = $('#inputMaxCpa').val();
    var PublisherPlatforms = $("#selectPublisherPlatforms").val();

    //定位已经选了的广告系列，存进数组
    var checkedTr = $("#tbody_facebook input:checked").parent();
    var adsGroup = [];
    checkedTr.each(function (idx) {
        var group = {};
        group.groupId = $(this).children("td:eq(0)").text();
        group.title = $(this).children("td:eq(2)").text();
        group.message = $(this).children("td:eq(3)").text();
        adsGroup.push(group);
    });

    var imagePath = $('#inputImagePath').val();
    var videoPath = $('#inputVideoPath').val();

    var app = null;
    for (var i = 0; i < appList.length; i++) {
        if (appList[i].tag_name == appName) {
            app = appList[i];
            break;
        }
    }

    //“分离到系列”作数组处理
    var explodeListImage = [];//{key:x, values:[]}
    //从图片路径和视频路径开始把List分裂
    if ($("#inputImagePath").val() || $("#inputImagePath").prop("checked")) {
        explodeListImage.push({
            key: "identification",
            values: ["image"]
        }, {
            key: "appName",
            values: [appName]
        }, {
            key: "accountName",
            values: [accountName.join(",")]
        }, {
            key: "accountId",
            values: [accountId.join(",")]
        }, {
            key: "createCount",
            values: [createCount]
        }, {
            key: "excludedRegion",
            values: [excludedRegion.join(",")]
        }, {
            key: "language",
            values: [language]
        }, {
            key: "interest",
            values: [interest]
        }, {
            key: "bugdet",
            values: [bugdet]
        }, {
            key: "bidStrategy",
            values: [bidStrategy]
        }, {
            key: "maxCPA",
            values: [maxCPA]
        }, {
            key: "appId",
            values: [app.fb_app_id]
        });

        if ($("#selectFBPageExplode").prop("checked")) {
            var FBarray = [];
            for (var j = 0; j < fbPage.length; j++) {
                var p = {};
                p.pageId = fbPage[j];
                p.pageName = fbPageName[j];
                FBarray.push(p);
            }
            explodeListImage.push({
                key: 'FBpage',
                values: FBarray
            });
        } else {
            var p = {};
            p.pageId = fbPage.join(",");
            p.pageName = fbPageName.join(",");
            explodeListImage.push({
                key: 'FBpage',
                values: [p]
            });
        }
        if ($("#selectRegionExplode").prop("checked")) {
            explodeListImage.push({
                key: 'region',
                values: region.map(function (x) {
                    return x.trim();
                })
            })
        } else {
            explodeListImage.push({
                key: 'region',
                values: [region.join(",")]
            })
        }
        if ($("#selectUserOsExplode").prop("checked")) {
            explodeListImage.push({
                key: 'userOs',
                values: userOs.map(function (x) {
                    return x.trim();
                })
            })
        } else {
            explodeListImage.push({
                key: 'userOs',
                values: [userOs.join(",")]
            })
        }
        if ($("#selectUserDevicesExplode").prop("checked")) {
            explodeListImage.push({
                key: 'userDevice',
                values: userDevice.split(',')
            })
        } else {
            explodeListImage.push({
                key: 'userDevice',
                values: [userDevice]
            })
        }
        //确保在从 campaigns_auto_create.jsp 跳转的情况下允许性别多选
        if (isAutoCreate && modifyRecordId > 0) {
            $("#selectGender").prop("checked", false);
        } else if (($("#selectGenderExplode").prop("checked") == false) && gender.length > 1) {
            admanager.showCommonDlg("错误", "不分离的情况下不允许性别多选");
            return false;
        }
        if ($("#selectGenderExplode").prop("checked") == true) {
            explodeListImage.push({
                key: 'gender',
                values: gender.map(function (x) {
                    return x.trim();
                })
            });
        } else {
            explodeListImage.push({
                key: 'gender',
                values: [gender.join(",")]
            });
        }

        if (!$("#inputAgeExplode").prop("checked") && age.indexOf(",") !== -1) {
            admanager.showCommonDlg("错误", "不分离的情况下不允许年龄多选");
            return false;
        }
        if ($("#inputAgeExplode").prop("checked")) {
            explodeListImage.push({
                key: 'age',
                values: age.split(",").map(function (x) {
                    return x.trim();
                })
            });
        } else {
            explodeListImage.push({
                key: 'age',
                values: [age]
            });
        }

        if (isAutoCreate && modifyRecordId > 0) {
            $("#inputBiddingExplode").prop("checked", false);
        } else if (!$("#inputBiddingExplode").prop("checked") && bidding.indexOf(",") !== -1) {
            admanager.showCommonDlg("错误", "不分离的情况下不允许出价多选");
            return false;
        }
        if ($("#inputBiddingExplode").prop("checked")) {
            explodeListImage.push({
                key: 'bidding',
                values: bidding.split(",").map(function (x) {
                    return x.trim();
                })
            });
        } else {
            explodeListImage.push({
                key: 'bidding',
                values: [bidding]
            });
        }
        if ($("#inputImageExplode").prop("checked")) {
            var valueList = imagePath.trim().replace(/\W*,\W*/g, ",").replace(/,$/, "").split(",");
            explodeListImage.push({
                key: 'materialPath',
                values: valueList
            });
        } else {
            var valueStr = imagePath.trim().replace(/,$/, "");
            explodeListImage.push({
                key: 'materialPath',
                values: [valueStr]
            });
        }
        explodeListImage.push({
            key: "adsGroup",
            values: adsGroup
        });
        if ($("#selectPublisherPlatformsExplode").prop("checked")) {
            explodeListImage.push({
                key: "publisherPlatforms",
                values: PublisherPlatforms
            });
        } else {
            var PublisherPlatformsString = PublisherPlatforms.join(",");
            explodeListImage.push({
                key: "publisherPlatforms",
                values: [PublisherPlatformsString]
            });
        }
    }
    var explodeListVideo = [];
    if ($("#inputVideoPath").val() || $("#inputVideoPath").prop("checked")) {
        explodeListVideo.push({
            key: "identification",
            values: ["video"]
        }, {
            key: "appName",
            values: [appName]
        }, {
            key: "accountName",
            values: [accountName.join(",")]
        }, {
            key: "accountId",
            values: [accountId.join(",")]
        }, {
            key: "createCount",
            values: [createCount]
        }, {
            key: "excludedRegion",
            values: [excludedRegion.join(",")]
        }, {
            key: "language",
            values: [language]
        }, {
            key: "interest",
            values: [interest]
        }, {
            key: "bugdet",
            values: [bugdet]
        }, {
            key: "bidStrategy",
            values: [bidStrategy]
        }, {
            key: "maxCPA",
            values: [maxCPA]
        }, {
            key: "appId",
            values: [app.fb_app_id]
        });

        if ($("#selectFBPageExplode").prop("checked")) {
            var FBarray = [];
            for (var j = 0; j < fbPage.length; j++) {
                var p = {};
                p.pageId = fbPage[j];
                p.pageName = fbPageName[j];
                FBarray.push(p);
            }
            explodeListVideo.push({
                key: 'FBpage',
                values: FBarray
            });
        } else {
            var p = {};
            p.pageId = fbPage.join(",");
            p.pageName = fbPageName.join(",");
            explodeListVideo.push({
                key: 'FBpage',
                values: [p]
            });
        }

        if ($("#selectRegionExplode").prop("checked")) {
            explodeListVideo.push({
                key: 'region',
                values: region.map(function (x) {
                    return x.trim();
                })
            })
        } else {
            explodeListVideo.push({
                key: 'region',
                values: [region.join(",")]
            })
        }
        if ($("#selectUserOsExplode").prop("checked")) {
            explodeListVideo.push({
                key: 'userOs',
                values: userOs.map(function (x) {
                    return x.trim();
                })
            })
        } else {
            explodeListVideo.push({
                key: 'userOs',
                values: [userOs.join(",")]
            })
        }
        if ($("#selectUserDevicesExplode").prop("checked")) {
            explodeListVideo.push({
                key: 'userDevice',
                values: userDevice.split(',')
            })
        } else {
            explodeListVideo.push({
                key: 'userDevice',
                values: [userDevice]
            })
        }
        //确保在从 campaigns_auto_create.jsp 跳转的情况下允许性别多选
        if (isAutoCreate && modifyRecordId > 0) {
            $("#selectGender").prop("checked", false);
        } else if (($("#selectGenderExplode").prop("checked") == false) && gender.length > 1) {
            admanager.showCommonDlg("错误", "不分离的情况下不允许性别多选");
            return false;
        }
        if ($("#selectGenderExplode").prop("checked") == true) {
            explodeListVideo.push({
                key: 'gender',
                values: gender.map(function (x) {
                    return x.trim();
                })
            });
        } else {
            explodeListVideo.push({
                key: 'gender',
                values: [gender.join(",")]
            });
        }

        if (!$("#inputAgeExplode").prop("checked") && age.indexOf(",") !== -1) {
            admanager.showCommonDlg("错误", "不分离的情况下不允许年龄多选");
            return false;
        }
        if ($("#inputAgeExplode").prop("checked")) {
            explodeListVideo.push({
                key: 'age',
                values: age.split(",").map(function (x) {
                    return x.trim();
                })
            });
        } else {
            explodeListVideo.push({
                key: 'age',
                values: [age]
            });
        }

        if (isAutoCreate && modifyRecordId > 0) {
            $("#inputBiddingExplode").prop("checked", false);
        } else if (!$("#inputBiddingExplode").prop("checked") && bidding.indexOf(",") !== -1) {
            admanager.showCommonDlg("错误", "不分离的情况下不允许出价多选");
            return false;
        }
        if ($("#inputBiddingExplode").prop("checked")) {
            explodeListVideo.push({
                key: 'bidding',
                values: bidding.split(",").map(function (x) {
                    return x.trim();
                })
            });
        } else {
            explodeListVideo.push({
                key: 'bidding',
                values: [bidding]
            });
        }
        if ($("#inputVideoExplode").prop("checked")) {
            var valueList = videoPath.trim().replace(/\W*,\W*/g, ",").replace(/,$/, "").split(",");
            explodeListVideo.push({
                key: 'materialPath',
                values: valueList
            });
        } else {
            var valueStr = videoPath.trim().replace(/,$/, "");
            explodeListVideo.push({
                key: 'materialPath',
                values: [valueStr]
            });
        }
        explodeListVideo.push({
            key: "adsGroup",
            values: adsGroup
        });
        if ($("#selectPublisherPlatformsExplode").prop("checked")) {
            explodeListVideo.push({
                key: "publisherPlatforms",
                values: PublisherPlatforms
            });
        } else {
            var PublisherPlatformsString = PublisherPlatforms.join(",");
            explodeListVideo.push({
                key: "publisherPlatforms",
                values: [PublisherPlatformsString]
            });
        }
    }
    var explodeParamsImage = explodeListImage.length > 0 ? explodeListImage.reduce(function (params, explodeParam) {
        return getExplodeParams(params, explodeParam);
    }, []) : [];
    var explodeParamsVideo = explodeListVideo.length > 0 ? explodeListVideo.reduce(function (params, explodeParam) {
        return getExplodeParams(params, explodeParam);
    }, []) : [];
    var explodeParams = explodeParamsImage.concat(explodeParamsVideo);
    explodeParams.forEach(function (p) {
        p.campaignName = generateFacebookCampaignName({
            identification: p.identification,
            fbPageName: p.FBpage.pageName,
            age: p.age,
            gender: p.gender,
            bidding: p.bidding,
            region: p.region,
            userOs: p.userOs,
            userDevice: p.userDevice,
            materialPath: p.materialPath, //改为material_path ,在后台再根据正则表达式匹配系列名决定存image还是video
            publisherPlatforms: p.publisherPlatforms,
            groupId: p.adsGroup.groupId
        });
        p.flag = flag;
    });

    return explodeParams;
}

//创建facebook系列
$('#btnCreate').click(function () {

    var bidding = $('#inputBidding').val();//出价/竞价

    var biddingMap = bidding.split(",").map(function (x) {
        return x.trim();
    })

    var flag = 0;
    biddingMap.forEach(function (one) {
        countryBidding.forEach(function (two) {
            if (one > two.bidding) {
                alert("你的出价大于 "+two.country+" 出价上限！请修改正确！");
                flag = 1;
                // return;
            }
        });
    })

    if (flag == 1) {
        return false;
    }


    var explodeParams = FacebookFormReading();
    //用 explodeParams 构造新的请求

    var checkAutoCreate = $('#checkAutoCreate').prop('checked');
    var onlyAutoCreateCheck = $('#onlyCheckAutoCreate').prop('checked');
    var flag = $('#flag').prop('checked');

    if (!checkAutoCreate && !onlyAutoCreateCheck && !flag) {
        var requestPool = [];
        explodeParams.forEach(function (p) {    //拆分好的键值对数组
            var cloned = {};
            $.extend(cloned, p);
            cloned.groupId = p.adsGroup.groupId;
            cloned.title = p.adsGroup.title;
            cloned.message = p.adsGroup.message;
            requestPool.push(cloned);
        });
        var bFinished = false;
        //针对 campaign.java 内部产生的Media错误导致的删除
        var warning = [];
        batchRequest(requestPool, function (param, onSuccess, onFail) {
            //fake
            // console.log("start.. ", param);
            $.post("campaign/create", param, function (data) {
                if (data && data.ret == 1) {
                    onSuccess();
                    if (data.warning) {
                        warning.push(data.warning);
                    }
                } else {
                    onFail(data.message)
                }
            }, "json");
        }, function (errorLog) {
            //手动队列全部处理完成
            if (warning.length > 0) {
                warning = warning.join("\n");
                admanager.showCommonDlg("部分系列创建失败", warning);
            } else {

            }
        });
    } else if (checkAutoCreate && !onlyAutoCreateCheck && !flag) {
        var requestPool = [];
        explodeParams.forEach(function (p) {    //拆分好的键值对数组
            var cloned = {};
            $.extend(cloned, p);
            cloned.groupId = p.adsGroup.groupId;
            cloned.title = p.adsGroup.title;
            cloned.message = p.adsGroup.message;
            requestPool.push(cloned);
        });
        var bFinished = false;
        //针对 campaign.java 内部产生的Media错误导致的删除
        var warning = [];
        batchRequest(requestPool, function (param, onSuccess, onFail) {
            //fake
            // console.log("start.. ", param);
            $.post("campaign/create", param, function (data) {
                if (data && data.ret == 1) {
                    onSuccess();
                    if (data.warning) {
                        warning.push(data.warning);
                    }
                } else {
                    onFail(data.message)
                }
            }, "json");
        }, function (errorLog) {
            //手动队列全部处理完成
            if (warning.length > 0) {
                warning = warning.join("\n");
                admanager.showCommonDlg("部分系列创建失败", warning);
            } else {

                if (!bFinished && errorLog && errorLog.length == 0) {
                    // bFinished = true;
                    var AutoRequestPool = [];
                    var url = "auto_create_campaign/facebook/create";
                    if (isAutoCreate && modifyRecordId > 0) {
                        requestPool.forEach(function (p) {
                            p.push({
                                key: id,
                                values: modifyRecordId
                            });
                        });
                        url = "auto_create_campaign/facebook/modify";
                    }
                    // var messageBody = "创建成功";
                    requestPool.forEach(function (p) {
                        var AutoCloned = {};
                        $.extend(AutoCloned, p);
                        AutoCloned.explodeCountry = $("#selectRegionExplode").prop("checked");
                        AutoCloned.explodeBidding = $("#inputBiddingExplode").prop("checked");
                        AutoCloned.explodeAge = $("#inputAgeExplode").prop("checked");
                        AutoCloned.explodeGender = $("#selectGenderExplode").prop("checked");
                        AutoRequestPool.push(AutoCloned);
                    });
                    batchRequest(AutoRequestPool, function (param, onSuccess, onFail) {
                        //fake
                        console.log("start.. ", param);
                        $.post(url, param, function (data) {
                            if (data && data.ret == 1) {
                                onSuccess();
                            } else {
                                onFail(data.message)
                            }
                        }, "json");
                    }, function () {
                        //[仅设置为自动创建]队列全部处理完成
                        layer.tips("自动创建队列处理完毕", "#btnCreate", {tips: 1, time: 3000});
                    });
                }
            }
        });

    } else if (!checkAutoCreate && onlyAutoCreateCheck && !flag) {

        var onlyAutoRequestPool = [];
        var explodeCountry = $("#selectRegionExplode").prop("checked");
        var explodeAge = $("#inputAgeExplode").prop("checked");
        var explodeGender = $("#selectGenderExplode").prop("checked");
        var explodeBidding = $("#inputBiddingExplode").prop("checked");
        var url = "auto_create_campaign/facebook/create";
        if (isAutoCreate && modifyRecordId > 0) {
            explodeParams.forEach(function (p) {
                p.id = modifyRecordId;
            });
            url = "auto_create_campaign/facebook/modify";
        }
        explodeParams.forEach(function (p) {
            var onlyAutoCloned = {};
            $.extend(onlyAutoCloned, p);
            onlyAutoCloned.explodeCountry = explodeCountry;
            onlyAutoCloned.explodeBidding = explodeBidding;
            onlyAutoCloned.explodeAge = explodeAge;
            onlyAutoCloned.explodeGender = explodeGender;
            onlyAutoCloned.groupId = p.adsGroup.groupId;
            onlyAutoCloned.title = p.adsGroup.title;
            onlyAutoCloned.message = p.adsGroup.message;
            onlyAutoRequestPool.push(onlyAutoCloned);
        });
        batchRequest(onlyAutoRequestPool, function (param, onSuccess, onFail) {
            //fake
            // console.log("start.. ", param);
            $.post(url, param, function (data) {
                if (data && data.ret == 1) {
                    onSuccess();
                } else {
                    onFail(data.message)
                }
            }, "json");
        }, function (errorLog) {
            //[仅设置为自动创建]队列全部处理完成
            if (isAutoCreate && modifyRecordId > 0) {
                layer.tips("更新队列处理完毕", "#btnCreate", {tips: 1, time: 3000});
            } else {
                layer.tips("自动创建队列处理完毕", "#btnCreate", {tips: 1, time: 3000});
            }
        });
    } else if (!checkAutoCreate && !onlyAutoCreateCheck && flag) {
        var requestPool = [];
        explodeParams.forEach(function (p) {    //拆分好的键值对数组
            var cloned = {};
            $.extend(cloned, p);
            cloned.groupId = p.adsGroup.groupId;
            cloned.title = p.adsGroup.title;
            cloned.message = p.adsGroup.message;
            requestPool.push(cloned);
        });
        var bFinished = false;
        //针对 campaign.java 内部产生的Media错误导致的删除
        var warning = [];
        batchRequest(requestPool, function (param, onSuccess, onFail) {
            //fake
            // console.log("start.. ", param);
            $.post("campaign/create", param, function (data) {
                if (data && data.ret == 1) {
                    onSuccess();
                    if (data.warning) {
                        warning.push(data.warning);
                    }
                } else {
                    onFail(data.message)
                }
            }, "json");
        }, function (errorLog) {
            //手动队列全部处理完成
            if (warning.length > 0) {
                warning = warning.join("\n");
                admanager.showCommonDlg("部分系列创建失败", warning);
            } else {

                if (!bFinished && errorLog && errorLog.length == 0) {
                    // bFinished = true;
                    var AutoRequestPool = [];
                    var url = "auto_create_campaign/facebook/create2";
                    if (isAutoCreate && modifyRecordId > 0) {
                        requestPool.forEach(function (p) {
                            p.push({
                                key: id,
                                values: modifyRecordId
                            });
                        });
                        url = "auto_create_campaign/facebook/modify";
                    }
                    // var messageBody = "创建成功";
                    requestPool.forEach(function (p) {
                        var AutoCloned = {};
                        $.extend(AutoCloned, p);
                        AutoCloned.explodeCountry = $("#selectRegionExplode").prop("checked");
                        AutoCloned.explodeBidding = $("#inputBiddingExplode").prop("checked");
                        AutoCloned.explodeAge = $("#inputAgeExplode").prop("checked");
                        AutoCloned.explodeGender = $("#selectGenderExplode").prop("checked");
                        AutoRequestPool.push(AutoCloned);
                    });
                    batchRequest(AutoRequestPool, function (param, onSuccess, onFail) {
                        //fake
                        console.log("start.. ", param);
                        $.post(url, param, function (data) {
                            if (data && data.ret == 1) {
                                onSuccess();
                            } else {
                                onFail(data.message)
                            }
                        }, "json");
                    }, function () {
                        //[仅设置为自动创建]队列全部处理完成
                        layer.tips("自动创建队列处理完毕", "#btnCreate", {tips: 1, time: 3000});
                    });
                }
            }
        });
    } else if (checkAutoCreate && !onlyAutoCreateCheck && flag) {

        var requestPool = [];
        explodeParams.forEach(function (p) {    //拆分好的键值对数组
            var cloned = {};
            $.extend(cloned, p);
            cloned.groupId = p.adsGroup.groupId;
            cloned.title = p.adsGroup.title;
            cloned.message = p.adsGroup.message;
            requestPool.push(cloned);
        });
        var bFinished = false;
        //针对 campaign.java 内部产生的Media错误导致的删除
        var warning = [];
        batchRequest(requestPool, function (param, onSuccess, onFail) {
            //fake
            // console.log("start.. ", param);
            $.post("campaign/create", param, function (data) {
                if (data && data.ret == 1) {
                    onSuccess();
                    if (data.warning) {
                        warning.push(data.warning);
                    }
                } else {
                    onFail(data.message)
                }
            }, "json");
        }, function (errorLog) {
            //手动队列全部处理完成
            if (warning.length > 0) {
                warning = warning.join("\n");
                admanager.showCommonDlg("部分系列创建失败", warning);
            } else {

                if (!bFinished && errorLog && errorLog.length == 0) {
                    // bFinished = true;
                    var AutoRequestPool = [];
                    var url = "auto_create_campaign/facebook/create";
                    if (isAutoCreate && modifyRecordId > 0) {
                        requestPool.forEach(function (p) {
                            p.push({
                                key: id,
                                values: modifyRecordId
                            });
                        });
                        url = "auto_create_campaign/facebook/modify";
                    }
                    // var messageBody = "创建成功";
                    requestPool.forEach(function (p) {
                        var AutoCloned = {};
                        $.extend(AutoCloned, p);
                        AutoCloned.explodeCountry = $("#selectRegionExplode").prop("checked");
                        AutoCloned.explodeBidding = $("#inputBiddingExplode").prop("checked");
                        AutoCloned.explodeAge = $("#inputAgeExplode").prop("checked");
                        AutoCloned.explodeGender = $("#selectGenderExplode").prop("checked");
                        AutoRequestPool.push(AutoCloned);
                    });
                    batchRequest(AutoRequestPool, function (param, onSuccess, onFail) {
                        //fake
                        console.log("start.. ", param);
                        $.post(url, param, function (data) {
                            if (data && data.ret == 1) {
                                onSuccess();
                            } else {
                                onFail(data.message)
                            }
                        }, "json");
                    }, function () {

                        //手动队列全部处理完成
                        if (warning.length > 0) {
                            warning = warning.join("\n");
                            admanager.showCommonDlg("部分系列创建失败", warning);
                        } else {

                            if (!bFinished && errorLog && errorLog.length == 0) {
                                // bFinished = true;
                                var AutoRequestPool = [];
                                var url = "auto_create_campaign/facebook/create2";
                                if (isAutoCreate && modifyRecordId > 0) {
                                    requestPool.forEach(function (p) {
                                        p.push({
                                            key: id,
                                            values: modifyRecordId
                                        });
                                    });
                                    url = "auto_create_campaign/facebook/modify";
                                }
                                // var messageBody = "创建成功";
                                requestPool.forEach(function (p) {
                                    var AutoCloned = {};
                                    $.extend(AutoCloned, p);
                                    AutoCloned.explodeCountry = $("#selectRegionExplode").prop("checked");
                                    AutoCloned.explodeBidding = $("#inputBiddingExplode").prop("checked");
                                    AutoCloned.explodeAge = $("#inputAgeExplode").prop("checked");
                                    AutoCloned.explodeGender = $("#selectGenderExplode").prop("checked");
                                    AutoRequestPool.push(AutoCloned);
                                });
                                batchRequest(AutoRequestPool, function (param, onSuccess, onFail) {
                                    //fake
                                    console.log("start.. ", param);
                                    $.post(url, param, function (data) {
                                        if (data && data.ret == 1) {
                                            onSuccess();
                                        } else {
                                            onFail(data.message)
                                        }
                                    }, "json");
                                }, function () {
                                    //[仅设置为自动创建]队列全部处理完成
                                    layer.tips("自动创建队列处理完毕", "#btnCreate", {tips: 1, time: 3000});
                                });
                            }
                        }

                    });
                }
            }
        });

    } else if (!checkAutoCreate && onlyAutoCreateCheck && flag) {


        var onlyAutoRequestPool = [];
        var explodeCountry = $("#selectRegionExplode").prop("checked");
        var explodeAge = $("#inputAgeExplode").prop("checked");
        var explodeGender = $("#selectGenderExplode").prop("checked");
        var explodeBidding = $("#inputBiddingExplode").prop("checked");
        var url = "auto_create_campaign/facebook/create";
        if (isAutoCreate && modifyRecordId > 0) {
            explodeParams.forEach(function (p) {
                p.id = modifyRecordId;
            });
            url = "auto_create_campaign/facebook/modify";
        }
        explodeParams.forEach(function (p) {
            var onlyAutoCloned = {};
            $.extend(onlyAutoCloned, p);
            onlyAutoCloned.explodeCountry = explodeCountry;
            onlyAutoCloned.explodeBidding = explodeBidding;
            onlyAutoCloned.explodeAge = explodeAge;
            onlyAutoCloned.explodeGender = explodeGender;
            onlyAutoCloned.groupId = p.adsGroup.groupId;
            onlyAutoCloned.title = p.adsGroup.title;
            onlyAutoCloned.message = p.adsGroup.message;
            onlyAutoRequestPool.push(onlyAutoCloned);
        });
        batchRequest(onlyAutoRequestPool, function (param, onSuccess, onFail) {
            //fake
            // console.log("start.. ", param);
            $.post(url, param, function (data) {
                if (data && data.ret == 1) {
                    onSuccess();
                } else {
                    onFail(data.message)
                }
            }, "json");
        }, function (errorLog) {
            //[仅设置为自动创建]队列全部处理完成

            var onlyAutoRequestPool = [];
            var explodeCountry = $("#selectRegionExplode").prop("checked");
            var explodeAge = $("#inputAgeExplode").prop("checked");
            var explodeGender = $("#selectGenderExplode").prop("checked");
            var explodeBidding = $("#inputBiddingExplode").prop("checked");
            var url = "auto_create_campaign/facebook/create2";
            if (isAutoCreate && modifyRecordId > 0) {
                explodeParams.forEach(function (p) {
                    p.id = modifyRecordId;
                });
                url = "auto_create_campaign/facebook/modify";
            }
            explodeParams.forEach(function (p) {
                var onlyAutoCloned = {};
                $.extend(onlyAutoCloned, p);
                onlyAutoCloned.explodeCountry = explodeCountry;
                onlyAutoCloned.explodeBidding = explodeBidding;
                onlyAutoCloned.explodeAge = explodeAge;
                onlyAutoCloned.explodeGender = explodeGender;
                onlyAutoCloned.groupId = p.adsGroup.groupId;
                onlyAutoCloned.title = p.adsGroup.title;
                onlyAutoCloned.message = p.adsGroup.message;
                onlyAutoRequestPool.push(onlyAutoCloned);
            });
            batchRequest(onlyAutoRequestPool, function (param, onSuccess, onFail) {
                //fake
                // console.log("start.. ", param);
                $.post(url, param, function (data) {
                    if (data && data.ret == 1) {
                        onSuccess();
                    } else {
                        onFail(data.message)
                    }
                }, "json");
            }, function (errorLog) {
                //[仅设置为自动创建]队列全部处理完成
                if (isAutoCreate && modifyRecordId > 0) {
                    layer.tips("更新队列处理完毕", "#btnCreate", {tips: 1, time: 3000});
                } else {
                    layer.tips("自动创建队列处理完毕", "#btnCreate", {tips: 1, time: 3000});
                }
            });

        });

    }


// if (onlyAutoCreateCheck) {
//     var onlyAutoRequestPool = [];
//     var explodeCountry = $("#selectRegionExplode").prop("checked");
//     var explodeAge = $("#inputAgeExplode").prop("checked");
//     var explodeGender = $("#selectGenderExplode").prop("checked");
//     var explodeBidding = $("#inputBiddingExplode").prop("checked");
//     var url = "auto_create_campaign/facebook/create";
//     if (isAutoCreate && modifyRecordId > 0) {
//         explodeParams.forEach(function (p) {
//             p.id = modifyRecordId;
//         });
//         url = "auto_create_campaign/facebook/modify";
//     }
//     explodeParams.forEach(function (p) {
//         var onlyAutoCloned = {};
//         $.extend(onlyAutoCloned, p);
//         onlyAutoCloned.explodeCountry = explodeCountry;
//         onlyAutoCloned.explodeBidding = explodeBidding;
//         onlyAutoCloned.explodeAge = explodeAge;
//         onlyAutoCloned.explodeGender = explodeGender;
//         onlyAutoCloned.groupId = p.adsGroup.groupId;
//         onlyAutoCloned.title = p.adsGroup.title;
//         onlyAutoCloned.message = p.adsGroup.message;
//         onlyAutoRequestPool.push(onlyAutoCloned);
//     });
//     batchRequest(onlyAutoRequestPool, function (param, onSuccess, onFail) {
//         //fake
//         // console.log("start.. ", param);
//         $.post(url, param, function (data) {
//             if (data && data.ret == 1) {
//                 onSuccess();
//             } else {
//                 onFail(data.message)
//             }
//         }, "json");
//     }, function (errorLog) {
//         //[仅设置为自动创建]队列全部处理完成
//         if (isAutoCreate && modifyRecordId > 0) {
//             layer.tips("更新队列处理完毕", "#btnCreate", {tips: 1, time: 3000});
//         } else {
//             layer.tips("自动创建队列处理完毕", "#btnCreate", {tips: 1, time: 3000});
//         }
//     });
// } else {
//     var requestPool = [];
//     explodeParams.forEach(function (p) {    //拆分好的键值对数组
//         var cloned = {};
//         $.extend(cloned, p);
//         cloned.groupId = p.adsGroup.groupId;
//         cloned.title = p.adsGroup.title;
//         cloned.message = p.adsGroup.message;
//         requestPool.push(cloned);
//     });
//     var bFinished = false;
//     //针对 campaign.java 内部产生的Media错误导致的删除
//     var warning = [];
//     batchRequest(requestPool, function (param, onSuccess, onFail) {
//         //fake
//         // console.log("start.. ", param);
//         $.post("campaign/create", param, function (data) {
//             if (data && data.ret == 1) {
//                 onSuccess();
//                 if (data.warning) {
//                     warning.push(data.warning);
//                 }
//             } else {
//                 onFail(data.message)
//             }
//         }, "json");
//     }, function (errorLog) {
//         //手动队列全部处理完成
//         if (warning.length > 0) {
//             warning = warning.join("\n");
//             admanager.showCommonDlg("部分系列创建失败", warning);
//         } else {
//             var checked = $('#checkAutoCreate').prop('checked');
//             if (checked && !bFinished && errorLog && errorLog.length == 0) {
//                 // bFinished = true;
//                 var AutoRequestPool = [];
//                 var url = "auto_create_campaign/facebook/create";
//                 if (isAutoCreate && modifyRecordId > 0) {
//                     requestPool.forEach(function (p) {
//                         p.push({
//                             key: id,
//                             values: modifyRecordId
//                         });
//                     });
//                     url = "auto_create_campaign/facebook/modify";
//                 }
//                 // var messageBody = "创建成功";
//                 requestPool.forEach(function (p) {
//                     var AutoCloned = {};
//                     $.extend(AutoCloned, p);
//                     AutoCloned.explodeCountry = $("#selectRegionExplode").prop("checked");
//                     AutoCloned.explodeBidding = $("#inputBiddingExplode").prop("checked");
//                     AutoCloned.explodeAge = $("#inputAgeExplode").prop("checked");
//                     AutoCloned.explodeGender = $("#selectGenderExplode").prop("checked");
//                     AutoRequestPool.push(AutoCloned);
//                 });
//                 batchRequest(AutoRequestPool, function (param, onSuccess, onFail) {
//                     //fake
//                     console.log("start.. ", param);
//                     $.post(url, param, function (data) {
//                         if (data && data.ret == 1) {
//                             onSuccess();
//                         } else {
//                             onFail(data.message)
//                         }
//                     }, "json");
//                 }, function () {
//                     //[仅设置为自动创建]队列全部处理完成
//                     layer.tips("自动创建队列处理完毕", "#btnCreate", {tips: 1, time: 3000});
//                 });
//             }
//         }
//     });
// }
    return false;
});

//读取并分离adwords表单数据
function AdwordFormReading() {
    var appName = $('#selectAppAdmob').val();
    var selectOptionsAdmob = $('#selectAccountAdmob option:selected');
    var accountNameAdmob = [];
    var accountIdAdmob = [];
    selectOptionsAdmob.each(function () {
        accountNameAdmob.push($(this).text());
        accountIdAdmob.push($(this).val());
    });
    var createCountAdmob = $('#inputCreateCountAdmob').val();
    var region = $('#selectRegionAdmob').val();
    var excludedRegion = $('#selectRegionUnselectedAdmob').val();
    var language = $('#selectLanguageAdmob').val();
    var conversion_id = $('#selectIncidentAdmob').val();
    var bugdet = $('#inputBudgetAdmob').val();
    /************************************************************************/
    var flag = "";
    if ($('#flag1').prop('checked')) {
        flag = "1";
    }

    var bidding = $('#inputBiddingAdmob').val();
    var maxCPA = $('#inputMaxCpaAdmob').val();

    //得到选中行的广告语信息
    var checkedTr = $("#tbody_admob input:checked").parent();
    var adsGroup = [];
    checkedTr.each(function (idx) {
        var group = {};
        group.groupId = $(this).children("td:eq(0)").text();
        group.message1 = $(this).children("td:eq(2)").text();
        group.message2 = $(this).children("td:eq(3)").text();
        group.message3 = $(this).children("td:eq(4)").text();
        group.message4 = $(this).children("td:eq(5)").text();
        adsGroup.push(group);
    });
    var imagePath = $('#inputImagePathAdmob').val();
    var app = null;
    for (var i = 0; i < appList.length; i++) {
        if (appList[i].tag_name == appName) {
            app = appList[i];
            break;
        }
    }
    //处理分离到系列的字段
    var explodeList = [];//{key:x, values:[]}
    explodeList.push({
        key: "appName",
        values: [appName]
    }, {
        key: "accountId",
        values: [accountIdAdmob.join(",")]
    }, {
        key: "accountName",
        values: [accountNameAdmob.join(",")]
    }, {
        key: "createCount",
        values: [createCountAdmob]
    }, {
        key: "excludedRegion",
        values: [excludedRegion.join(',')]
    }, {
        key: "language",
        values: [language]
    }, {
        key: "conversion_id",
        values: [conversion_id]
    }, {
        key: "bugdet",
        values: [bugdet]
    }, {
        key: "gpPackageId",
        values: [app.google_package_id]
    }, {
        key: "maxCPA",
        values: [maxCPA]
    }, {
        key: "adsGroup",
        values: adsGroup
    });
    if ($("#selectRegionAdmobExplode").prop("checked")) {
        explodeList.push({
            key: 'region',
            values: region.map(function (x) {
                return x.trim();
            })
        })
    } else {
        explodeList.push({
            key: 'region',
            values: [region.join(",")]
        })
    }
    if (isAutoCreate && modifyRecordId > 0) {
        $("#inputBiddingAdmobExplode").prop("checked", false);
    } else if (!$("#inputBiddingAdmobExplode").prop("checked") && bidding.indexOf(",") !== -1) {
        admanager.showCommonDlg("错误", "不分离的情况下不允许出价多选");
        return false;
    }
    if ($("#inputBiddingAdmobExplode").prop("checked")) {
        explodeList.push({
            key: 'bidding',
            values: bidding.split(",").map(function (x) {
                return x.trim();
            })
        });
    } else {
        explodeList.push({
            key: 'bidding',
            values: [bidding]
        });
    }
    //处理图片路径
    if ($("#inputImageAdmobExplode").prop("checked")) {
        var valueList = imagePath.trim().replace(/\W*,\W*/g, ",").replace(/,$/, "").split(",");    //确保正确地切分为数组
        explodeList.push({
            key: 'imagePath',
            values: valueList
        });
    } else {
        var valueStr = imagePath.trim().replace(/,$/, "");
        explodeList.push({
            key: 'imagePath',
            values: [valueStr]
        });
    }

    var explodeParams = explodeList.length > 0 ? explodeList.reduce(function (params, explodeParam) {
        return getExplodeParams(params, explodeParam);
    }, []) : [];
    explodeParams.forEach(function (p) {
        p.campaignName = generateAdmobCampaignName({  //动态生成系列名字
            bidding: p.bidding,
            region: p.region,
            imagePath: p.imagePath,
            groupId: p.adsGroup.groupId
        });
        p.flag = flag;
    });
    return explodeParams;
}

//创建admob系列
$("#btnCreateAdmob").click(function () {
    //对创建的出价进行校验
    var bidding = $('#inputBiddingAdmob').val();//出价/竞价
    var biddingMap2 = bidding.split(",").map(function (x) {
        return x.trim();
    })
    var flag = 0;
    biddingMap2.forEach(function (one) {
        countryBiddingAdmob.forEach(function (two) {
            if (one > two.bidding) {
                alert("你的出价大于 "+two.country+" 出价上限！请修改正确！");
                flag = 1;
                return;
            }
        });
    })
    if (flag == 1) {
        return;
    }

    var explodeParams = AdwordFormReading();

    var checkAutoCreate = $('#checkAdmobAutoCreate').prop('checked');
    var onlyAutoCreateCheck = $('#onlyCheckAdmobAutoCreate').prop('checked');
    var flag = $('#flag1').prop('checked');

    if (!checkAutoCreate && !onlyAutoCreateCheck && !flag) {
        var requestPool = [];
        explodeParams.forEach(function (p) {
            var cloned = {}
            $.extend(cloned, p);
            cloned.groupId = p.adsGroup.groupId;
            cloned.message1 = p.adsGroup.message1;
            cloned.message2 = p.adsGroup.message2;
            cloned.message3 = p.adsGroup.message3;
            cloned.message4 = p.adsGroup.message4;
            requestPool.push(cloned);
        });
        var bFinished = false;
        batchRequest(requestPool, function (param, onSuccess, onFail) {
            //fake
            console.log("start.. ", param);
            $.post("campaign_admob/create", param, function (data) {
                if (data && data.ret == 1) {
                    onSuccess();
                } else {
                    onFail(data.message)
                }
            }, "json");
        }, function (errorLog) {

        });
    } else if (checkAutoCreate && !onlyAutoCreateCheck && !flag) {

        var requestPool = [];
        explodeParams.forEach(function (p) {
            var cloned = {}
            $.extend(cloned, p);
            cloned.groupId = p.adsGroup.groupId;
            cloned.message1 = p.adsGroup.message1;
            cloned.message2 = p.adsGroup.message2;
            cloned.message3 = p.adsGroup.message3;
            cloned.message4 = p.adsGroup.message4;
            requestPool.push(cloned);
        });
        var bFinished = false;
        batchRequest(requestPool, function (param, onSuccess, onFail) {
            //fake
            console.log("start.. ", param);
            $.post("campaign_admob/create", param, function (data) {
                if (data && data.ret == 1) {
                    onSuccess();
                } else {
                    onFail(data.message)
                }
            }, "json");
        }, function (errorLog) {
            //队列全部处理完成
            var AutoRequestPool = [];
            var explodeCountry = $("#selectRegionAdmobExplode").prop("checked");
            var explodeBidding = $("#inputBiddingAdmobExplode").prop("checked");
            requestPool.forEach(function (p) {
                var AutoCloned = {};
                $.extend(AutoCloned, p);
                AutoCloned.explodeCountry = explodeCountry;
                AutoCloned.explodeBidding = explodeBidding;
                AutoCloned.groupId = p.adsGroup.groupId;
                AutoCloned.message1 = p.adsGroup.message1;
                AutoCloned.message2 = p.adsGroup.message2;
                AutoCloned.message3 = p.adsGroup.message3;
                AutoCloned.message4 = p.adsGroup.message4;
                AutoRequestPool.push(AutoCloned);
            });
            var url = "auto_create_campaign/adwords/create";
            if (isAutoCreate && modifyRecordId > 0) {
                AutoRequestPool.forEach(function (p) {
                    p.id = modifyRecordId;
                });
                url = "auto_create_campaign/adwords/modify";
            }
            batchRequest(AutoRequestPool, function (param, onSuccess, onFail) {
                $.post(url, param, function (data) {
                    if (data && data.ret == 1) {
                        onSuccess();
                    } else {
                        onFail(data.message)
                    }
                }, "json");
            }, function (errorLog) {
                //[设置为自动创建]队列全部处理完成
                layer.tips("自动创建队列处理完毕", "#btnCreateAdmob", {tips: 1, time: 3000});
            });

        });

    } else if (!checkAutoCreate && onlyAutoCreateCheck && !flag) {
        var onlyAutoRequestPool = [];
        var explodeCountry = $("#selectRegionAdmobExplode").prop("checked");  //这两个量是选中【仅设为自动创建
        var explodeBidding = $("#inputBiddingAdmobExplode").prop("checked");
        var url = "auto_create_campaign/adwords/create";
        if (isAutoCreate && modifyRecordId > 0) {
            explodeParams.forEach(function (p) {
                p.id = modifyRecordId;
            });
            url = "auto_create_campaign/adwords/modify";
        }
        explodeParams.forEach(function (p) {
            var onlyAutoCloned = {};
            $.extend(onlyAutoCloned, p);
            onlyAutoCloned.explodeCountry = explodeCountry;
            onlyAutoCloned.explodeBidding = explodeBidding;
            onlyAutoCloned.groupId = p.adsGroup.groupId;
            onlyAutoCloned.message1 = p.adsGroup.message1;
            onlyAutoCloned.message2 = p.adsGroup.message2;
            onlyAutoCloned.message3 = p.adsGroup.message3;
            onlyAutoCloned.message4 = p.adsGroup.message4;
            onlyAutoRequestPool.push(onlyAutoCloned);
        });
        batchRequest(onlyAutoRequestPool, function (param, onSuccess, onFail) {
            $.post(url, param, function (data) {
                if (data && data.ret == 1) {
                    onSuccess();
                } else {
                    onFail(data.message)
                }
            }, "json");
        }, function (errorLog) {
            //[仅设置为自动创建]队列全部处理完成
            layer.tips("仅自动创建队列处理完毕", "#btnCreateAdmob", {tips: 1, time: 2000});
        });
    } else if (!checkAutoCreate && !onlyAutoCreateCheck && flag) {

        var requestPool = [];
        explodeParams.forEach(function (p) {
            var cloned = {}
            $.extend(cloned, p);
            cloned.groupId = p.adsGroup.groupId;
            cloned.message1 = p.adsGroup.message1;
            cloned.message2 = p.adsGroup.message2;
            cloned.message3 = p.adsGroup.message3;
            cloned.message4 = p.adsGroup.message4;
            requestPool.push(cloned);
        });
        var bFinished = false;
        batchRequest(requestPool, function (param, onSuccess, onFail) {
            //fake
            console.log("start.. ", param);
            $.post("campaign_admob/create", param, function (data) {
                if (data && data.ret == 1) {
                    onSuccess();
                } else {
                    onFail(data.message)
                }
            }, "json");
        }, function (errorLog) {
            //队列全部处理完成
            var AutoRequestPool = [];
            var explodeCountry = $("#selectRegionAdmobExplode").prop("checked");
            var explodeBidding = $("#inputBiddingAdmobExplode").prop("checked");
            requestPool.forEach(function (p) {
                var AutoCloned = {};
                $.extend(AutoCloned, p);
                AutoCloned.explodeCountry = explodeCountry;
                AutoCloned.explodeBidding = explodeBidding;
                AutoCloned.groupId = p.adsGroup.groupId;
                AutoCloned.message1 = p.adsGroup.message1;
                AutoCloned.message2 = p.adsGroup.message2;
                AutoCloned.message3 = p.adsGroup.message3;
                AutoCloned.message4 = p.adsGroup.message4;
                AutoRequestPool.push(AutoCloned);
            });
            var url = "auto_create_campaign/adwords/create2";
            if (isAutoCreate && modifyRecordId > 0) {
                AutoRequestPool.forEach(function (p) {
                    p.id = modifyRecordId;
                });
                url = "auto_create_campaign/adwords/modify";
            }
            batchRequest(AutoRequestPool, function (param, onSuccess, onFail) {
                $.post(url, param, function (data) {
                    if (data && data.ret == 1) {
                        onSuccess();
                    } else {
                        onFail(data.message)
                    }
                }, "json");
            }, function (errorLog) {
                //[设置为自动创建]队列全部处理完成
                layer.tips("自动创建队列处理完毕", "#btnCreateAdmob", {tips: 1, time: 3000});
            });
        });


    } else if (checkAutoCreate && !onlyAutoCreateCheck && flag) {


        var requestPool = [];
        explodeParams.forEach(function (p) {
            var cloned = {}
            $.extend(cloned, p);
            cloned.groupId = p.adsGroup.groupId;
            cloned.message1 = p.adsGroup.message1;
            cloned.message2 = p.adsGroup.message2;
            cloned.message3 = p.adsGroup.message3;
            cloned.message4 = p.adsGroup.message4;
            requestPool.push(cloned);
        });
        var bFinished = false;
        batchRequest(requestPool, function (param, onSuccess, onFail) {
            //fake
            console.log("start.. ", param);
            $.post("campaign_admob/create", param, function (data) {
                if (data && data.ret == 1) {
                    onSuccess();
                } else {
                    onFail(data.message)
                }
            }, "json");
        }, function (errorLog) {
            //队列全部处理完成
            var AutoRequestPool = [];
            var explodeCountry = $("#selectRegionAdmobExplode").prop("checked");
            var explodeBidding = $("#inputBiddingAdmobExplode").prop("checked");
            requestPool.forEach(function (p) {
                var AutoCloned = {};
                $.extend(AutoCloned, p);
                AutoCloned.explodeCountry = explodeCountry;
                AutoCloned.explodeBidding = explodeBidding;
                AutoCloned.groupId = p.adsGroup.groupId;
                AutoCloned.message1 = p.adsGroup.message1;
                AutoCloned.message2 = p.adsGroup.message2;
                AutoCloned.message3 = p.adsGroup.message3;
                AutoCloned.message4 = p.adsGroup.message4;
                AutoRequestPool.push(AutoCloned);
            });
            var url = "auto_create_campaign/adwords/create";
            if (isAutoCreate && modifyRecordId > 0) {
                AutoRequestPool.forEach(function (p) {
                    p.id = modifyRecordId;
                });
                url = "auto_create_campaign/adwords/modify";
            }
            batchRequest(AutoRequestPool, function (param, onSuccess, onFail) {
                $.post(url, param, function (data) {
                    if (data && data.ret == 1) {
                        onSuccess();
                    } else {
                        onFail(data.message)
                    }
                }, "json");
            }, function (errorLog) {
                //[设置为自动创建]队列全部处理完成
                var AutoRequestPool = [];
                var explodeCountry = $("#selectRegionAdmobExplode").prop("checked");
                var explodeBidding = $("#inputBiddingAdmobExplode").prop("checked");
                requestPool.forEach(function (p) {
                    var AutoCloned = {};
                    $.extend(AutoCloned, p);
                    AutoCloned.explodeCountry = explodeCountry;
                    AutoCloned.explodeBidding = explodeBidding;
                    AutoCloned.groupId = p.adsGroup.groupId;
                    AutoCloned.message1 = p.adsGroup.message1;
                    AutoCloned.message2 = p.adsGroup.message2;
                    AutoCloned.message3 = p.adsGroup.message3;
                    AutoCloned.message4 = p.adsGroup.message4;
                    AutoRequestPool.push(AutoCloned);
                });
                var url = "auto_create_campaign/adwords/create2";
                if (isAutoCreate && modifyRecordId > 0) {
                    AutoRequestPool.forEach(function (p) {
                        p.id = modifyRecordId;
                    });
                    url = "auto_create_campaign/adwords/modify";
                }
                batchRequest(AutoRequestPool, function (param, onSuccess, onFail) {
                    $.post(url, param, function (data) {
                        if (data && data.ret == 1) {
                            onSuccess();
                        } else {
                            onFail(data.message)
                        }
                    }, "json");
                }, function (errorLog) {
                    //[设置为自动创建]队列全部处理完成
                    layer.tips("自动创建队列处理完毕", "#btnCreateAdmob", {tips: 1, time: 3000});
                });
            });
        });


    } else if (!checkAutoCreate && onlyAutoCreateCheck && flag) {
        var onlyAutoRequestPool = [];
        var explodeCountry = $("#selectRegionAdmobExplode").prop("checked");  //这两个量是选中【仅设为自动创建
        var explodeBidding = $("#inputBiddingAdmobExplode").prop("checked");
        var url = "auto_create_campaign/adwords/create";
        if (isAutoCreate && modifyRecordId > 0) {
            explodeParams.forEach(function (p) {
                p.id = modifyRecordId;
            });
            url = "auto_create_campaign/adwords/modify";
        }
        explodeParams.forEach(function (p) {
            var onlyAutoCloned = {};
            $.extend(onlyAutoCloned, p);
            onlyAutoCloned.explodeCountry = explodeCountry;
            onlyAutoCloned.explodeBidding = explodeBidding;
            onlyAutoCloned.groupId = p.adsGroup.groupId;
            onlyAutoCloned.message1 = p.adsGroup.message1;
            onlyAutoCloned.message2 = p.adsGroup.message2;
            onlyAutoCloned.message3 = p.adsGroup.message3;
            onlyAutoCloned.message4 = p.adsGroup.message4;
            onlyAutoRequestPool.push(onlyAutoCloned);
        });
        batchRequest(onlyAutoRequestPool, function (param, onSuccess, onFail) {
            $.post(url, param, function (data) {
                if (data && data.ret == 1) {
                    onSuccess();
                } else {
                    onFail(data.message)
                }
            }, "json");
        }, function (errorLog) {
            //[仅设置为自动创建]队列全部处理完成
            var onlyAutoRequestPool = [];
            var explodeCountry = $("#selectRegionAdmobExplode").prop("checked");  //这两个量是选中【仅设为自动创建
            var explodeBidding = $("#inputBiddingAdmobExplode").prop("checked");
            var url = "auto_create_campaign/adwords/create2";
            if (isAutoCreate && modifyRecordId > 0) {
                explodeParams.forEach(function (p) {
                    p.id = modifyRecordId;
                });
                url = "auto_create_campaign/adwords/modify";
            }
            explodeParams.forEach(function (p) {
                var onlyAutoCloned = {};
                $.extend(onlyAutoCloned, p);
                onlyAutoCloned.explodeCountry = explodeCountry;
                onlyAutoCloned.explodeBidding = explodeBidding;
                onlyAutoCloned.groupId = p.adsGroup.groupId;
                onlyAutoCloned.message1 = p.adsGroup.message1;
                onlyAutoCloned.message2 = p.adsGroup.message2;
                onlyAutoCloned.message3 = p.adsGroup.message3;
                onlyAutoCloned.message4 = p.adsGroup.message4;
                onlyAutoRequestPool.push(onlyAutoCloned);
            });
            batchRequest(onlyAutoRequestPool, function (param, onSuccess, onFail) {
                $.post(url, param, function (data) {
                    if (data && data.ret == 1) {
                        onSuccess();
                    } else {
                        onFail(data.message)
                    }
                }, "json");
            }, function (errorLog) {
                //[仅设置为自动创建]队列全部处理完成
                layer.tips("仅自动创建队列处理完毕", "#btnCreateAdmob", {tips: 1, time: 2000});
            });
        });
    }


    /*********************************************************************************************************************/
    // if (onlyAutoCreateCheck) {
    //     var onlyAutoRequestPool = [];
    //     var explodeCountry = $("#selectRegionAdmobExplode").prop("checked");  //这两个量是选中【仅设为自动创建】特有的
    //     var explodeBidding = $("#inputBiddingAdmobExplode").prop("checked");
    //     var url = "auto_create_campaign/adwords/create";
    //     if (isAutoCreate && modifyRecordId > 0) {
    //         explodeParams.forEach(function (p) {
    //             p.id = modifyRecordId;
    //         });
    //         url = "auto_create_campaign/adwords/modify";
    //     }
    //     explodeParams.forEach(function (p) {
    //         var onlyAutoCloned = {};
    //         $.extend(onlyAutoCloned, p);
    //         onlyAutoCloned.explodeCountry = explodeCountry;
    //         onlyAutoCloned.explodeBidding = explodeBidding;
    //         onlyAutoCloned.groupId = p.adsGroup.groupId;
    //         onlyAutoCloned.message1 = p.adsGroup.message1;
    //         onlyAutoCloned.message2 = p.adsGroup.message2;
    //         onlyAutoCloned.message3 = p.adsGroup.message3;
    //         onlyAutoCloned.message4 = p.adsGroup.message4;
    //         onlyAutoRequestPool.push(onlyAutoCloned);
    //     });
    //     batchRequest(onlyAutoRequestPool, function (param, onSuccess, onFail) {
    //         $.post(url, param, function (data) {
    //             if (data && data.ret == 1) {
    //                 onSuccess();
    //             } else {
    //                 onFail(data.message)
    //             }
    //         }, "json");
    //     }, function (errorLog) {
    //         //[仅设置为自动创建]队列全部处理完成
    //         layer.tips("仅自动创建队列处理完毕", "#btnCreateAdmob", {tips: 1, time: 2000});
    //     });
    // } else {
    //     var requestPool = [];
    //     explodeParams.forEach(function (p) {
    //         var cloned = {}
    //         $.extend(cloned, p);
    //         cloned.groupId = p.adsGroup.groupId;
    //         cloned.message1 = p.adsGroup.message1;
    //         cloned.message2 = p.adsGroup.message2;
    //         cloned.message3 = p.adsGroup.message3;
    //         cloned.message4 = p.adsGroup.message4;
    //         requestPool.push(cloned);
    //     });
    //     var bFinished = false;
    //     batchRequest(requestPool, function (param, onSuccess, onFail) {
    //         //fake
    //         console.log("start.. ", param);
    //         $.post("campaign_admob/create", param, function (data) {
    //             if (data && data.ret == 1) {
    //                 onSuccess();
    //             } else {
    //                 onFail(data.message)
    //             }
    //         }, "json");
    //     }, function (errorLog) {
    //         //队列全部处理完成
    //         var checked = $('#checkAdmobAutoCreate').prop('checked');
    //         if (checked && !bFinished && errorLog && errorLog.length == 0) {
    //             // bFinished = true;
    //             var AutoRequestPool = [];
    //             var explodeCountry = $("#selectRegionAdmobExplode").prop("checked");
    //             var explodeBidding = $("#inputBiddingAdmobExplode").prop("checked");
    //             requestPool.forEach(function (p) {
    //                 var AutoCloned = {};
    //                 $.extend(AutoCloned, p);
    //                 AutoCloned.explodeCountry = explodeCountry;
    //                 AutoCloned.explodeBidding = explodeBidding;
    //                 AutoCloned.groupId = p.adsGroup.groupId;
    //                 AutoCloned.message1 = p.adsGroup.message1;
    //                 AutoCloned.message2 = p.adsGroup.message2;
    //                 AutoCloned.message3 = p.adsGroup.message3;
    //                 AutoCloned.message4 = p.adsGroup.message4;
    //                 AutoRequestPool.push(AutoCloned);
    //             });
    //             var url = "auto_create_campaign/adwords/create";
    //             if (isAutoCreate && modifyRecordId > 0) {
    //                 AutoRequestPool.forEach(function (p) {
    //                     p.id = modifyRecordId;
    //                 });
    //                 url = "auto_create_campaign/adwords/modify";
    //             }
    //             batchRequest(AutoRequestPool, function (param, onSuccess, onFail) {
    //                 $.post(url, param, function (data) {
    //                     if (data && data.ret == 1) {
    //                         onSuccess();
    //                     } else {
    //                         onFail(data.message)
    //                     }
    //                 }, "json");
    //             }, function (errorLog) {
    //                 //[设置为自动创建]队列全部处理完成
    //                 layer.tips("自动创建队列处理完毕", "#btnCreateAdmob", {tips: 1, time: 3000});
    //             });
    //         }
    //     });
    // }
    return false;
});

//在路径选了多个的情况下，用于决定是否默认"分离到系列"
$("#inputVideoPath,#inputImagePath,#inputImagePathAdmob").change(function () {
    function existMutipleSelection(str) {
        var array = str.trim().replace(/,$/, "").split(",");
        if (array.length > 1) {
            return true;
        } else {
            return false;
        }
    }

    var elementId = $(this).attr("id");
    if (elementId == "inputImagePath") {
        var val = $(this).val();
        if (existMutipleSelection(val)) {
            $("#inputImageExplode").prop("checked", true);
        } else {
            $("#inputImageExplode").prop("checked", false);
        }
    } else if (elementId == "inputVideoPath") {
        var val = $(this).val();
        if (existMutipleSelection(val)) {
            $("#inputVideoExplode").prop("checked", true);
        } else {
            $("#inputVideoExplode").prop("checked", false);
        }
    } else if (elementId == "inputImagePathAdmob") {
        var val = $(this).val();
        if (existMutipleSelection(val)) {
            $("#inputImageAdmobExplode").prop("checked", true);
        } else {
            $("#inputImageAdmobExplode").prop("checked", false);
        }
    }
});




