var regionList = [{"key":"AD","name":"Andorra","type":"country","country_code":"AD","supports_region":true,"supports_city":false},{"key":"AE","name":"United Arab Emirates","type":"country","country_code":"AE","supports_region":true,"supports_city":true},{"key":"AF","name":"Afghanistan","type":"country","country_code":"AF","supports_region":true,"supports_city":false},{"key":"AG","name":"Antigua","type":"country","country_code":"AG","supports_region":true,"supports_city":false},{"key":"AI","name":"Anguilla","type":"country","country_code":"AI","supports_region":true,"supports_city":false},{"key":"AL","name":"Albania","type":"country","country_code":"AL","supports_region":true,"supports_city":false},{"key":"AM","name":"Armenia","type":"country","country_code":"AM","supports_region":true,"supports_city":false},{"key":"AN","name":"Netherlands Antilles","type":"country","country_code":"AN","supports_region":false,"supports_city":false},{"key":"AO","name":"Angola","type":"country","country_code":"AO","supports_region":true,"supports_city":false},{"key":"AQ","name":"Antarctica","type":"country","country_code":"AQ","supports_region":false,"supports_city":false},{"key":"AR","name":"Argentina","type":"country","country_code":"AR","supports_region":true,"supports_city":true},{"key":"AS","name":"American Samoa","type":"country","country_code":"AS","supports_region":true,"supports_city":false},{"key":"AT","name":"Austria","type":"country","country_code":"AT","supports_region":true,"supports_city":true},{"key":"AU","name":"Australia","type":"country","country_code":"AU","supports_region":true,"supports_city":true},{"key":"AW","name":"Aruba","type":"country","country_code":"AW","supports_region":false,"supports_city":false},{"key":"AX","name":"Aland Islands","type":"country","country_code":"AX","supports_region":false,"supports_city":false},{"key":"AZ","name":"Azerbaijan","type":"country","country_code":"AZ","supports_region":true,"supports_city":false},{"key":"BA","name":"Bosnia and Herzegovina","type":"country","country_code":"BA","supports_region":false,"supports_city":false},{"key":"BB","name":"Barbados","type":"country","country_code":"BB","supports_region":true,"supports_city":false},{"key":"BD","name":"Bangladesh","type":"country","country_code":"BD","supports_region":true,"supports_city":false},{"key":"BE","name":"Belgium","type":"country","country_code":"BE","supports_region":true,"supports_city":true},{"key":"BF","name":"Burkina Faso","type":"country","country_code":"BF","supports_region":false,"supports_city":false},{"key":"BG","name":"Bulgaria","type":"country","country_code":"BG","supports_region":true,"supports_city":true},{"key":"BH","name":"Bahrain","type":"country","country_code":"BH","supports_region":true,"supports_city":false},{"key":"BI","name":"Burundi","type":"country","country_code":"BI","supports_region":true,"supports_city":false},{"key":"BJ","name":"Benin","type":"country","country_code":"BJ","supports_region":true,"supports_city":false},{"key":"BL","name":"Saint Barthélemy","type":"country","country_code":"BL","supports_region":false,"supports_city":false},{"key":"BM","name":"Bermuda","type":"country","country_code":"BM","supports_region":true,"supports_city":false},{"key":"BN","name":"Brunei","type":"country","country_code":"BN","supports_region":true,"supports_city":false},{"key":"BO","name":"Bolivia","type":"country","country_code":"BO","supports_region":true,"supports_city":true},{"key":"BQ","name":"Bonaire, Sint Eustatius and Saba","type":"country","country_code":"BQ","supports_region":false,"supports_city":false},{"key":"BR","name":"Brazil","type":"country","country_code":"BR","supports_region":true,"supports_city":true},{"key":"BS","name":"The Bahamas","type":"country","country_code":"BS","supports_region":false,"supports_city":false},{"key":"BT","name":"Bhutan","type":"country","country_code":"BT","supports_region":true,"supports_city":false},{"key":"BV","name":"Bouvet Island","type":"country","country_code":"BV","supports_region":false,"supports_city":false},{"key":"BW","name":"Botswana","type":"country","country_code":"BW","supports_region":true,"supports_city":false},{"key":"BY","name":"Belarus","type":"country","country_code":"BY","supports_region":true,"supports_city":false},{"key":"BZ","name":"Belize","type":"country","country_code":"BZ","supports_region":true,"supports_city":false},{"key":"CA","name":"Canada","type":"country","country_code":"CA","supports_region":true,"supports_city":true},{"key":"CC","name":"Cocos (Keeling) Islands","type":"country","country_code":"CC","supports_region":false,"supports_city":false},{"key":"CD","name":"Democratic Republic of the Congo","type":"country","country_code":"CD","supports_region":false,"supports_city":false},{"key":"CF","name":"Central African Republic","type":"country","country_code":"CF","supports_region":false,"supports_city":false},{"key":"CG","name":"Republic of the Congo","type":"country","country_code":"CG","supports_region":true,"supports_city":false},{"key":"CH","name":"Switzerland","type":"country","country_code":"CH","supports_region":true,"supports_city":true},{"key":"CI","name":"Côte d'Ivoire","type":"country","country_code":"CI","supports_region":true,"supports_city":false},{"key":"CK","name":"Cook Islands","type":"country","country_code":"CK","supports_region":true,"supports_city":false},{"key":"CL","name":"Chile","type":"country","country_code":"CL","supports_region":true,"supports_city":true},{"key":"CM","name":"Cameroon","type":"country","country_code":"CM","supports_region":true,"supports_city":false},{"key":"CN","name":"China","type":"country","country_code":"CN","supports_region":false,"supports_city":false},{"key":"CO","name":"Colombia","type":"country","country_code":"CO","supports_region":true,"supports_city":true},{"key":"CR","name":"Costa Rica","type":"country","country_code":"CR","supports_region":true,"supports_city":true},{"key":"CV","name":"Cape Verde","type":"country","country_code":"CV","supports_region":true,"supports_city":false},{"key":"CW","name":"Curaçao","type":"country","country_code":"CW","supports_region":false,"supports_city":false},{"key":"CX","name":"Christmas Island","type":"country","country_code":"CX","supports_region":false,"supports_city":false},{"key":"CY","name":"Cyprus","type":"country","country_code":"CY","supports_region":true,"supports_city":false},{"key":"CZ","name":"Czech Republic","type":"country","country_code":"CZ","supports_region":true,"supports_city":true},{"key":"DE","name":"Germany","type":"country","country_code":"DE","supports_region":true,"supports_city":true},{"key":"DJ","name":"Djibouti","type":"country","country_code":"DJ","supports_region":true,"supports_city":false},{"key":"DK","name":"Denmark","type":"country","country_code":"DK","supports_region":true,"supports_city":true},{"key":"DM","name":"Dominica","type":"country","country_code":"DM","supports_region":true,"supports_city":false},{"key":"DO","name":"Dominican Republic","type":"country","country_code":"DO","supports_region":true,"supports_city":true},{"key":"DZ","name":"Algeria","type":"country","country_code":"DZ","supports_region":true,"supports_city":false},{"key":"EC","name":"Ecuador","type":"country","country_code":"EC","supports_region":true,"supports_city":true},{"key":"EE","name":"Estonia","type":"country","country_code":"EE","supports_region":true,"supports_city":false},{"key":"EG","name":"Egypt","type":"country","country_code":"EG","supports_region":true,"supports_city":true},{"key":"EH","name":"Western Sahara","type":"country","country_code":"EH","supports_region":false,"supports_city":false},{"key":"ER","name":"Eritrea","type":"country","country_code":"ER","supports_region":true,"supports_city":false},{"key":"ES","name":"Spain","type":"country","country_code":"ES","supports_region":true,"supports_city":true},{"key":"ET","name":"Ethiopia","type":"country","country_code":"ET","supports_region":true,"supports_city":false},{"key":"FI","name":"Finland","type":"country","country_code":"FI","supports_region":true,"supports_city":true},{"key":"FJ","name":"Fiji","type":"country","country_code":"FJ","supports_region":true,"supports_city":false},{"key":"FK","name":"Falkland Islands","type":"country","country_code":"FK","supports_region":false,"supports_city":false},{"key":"FM","name":"Federated States of Micronesia","type":"country","country_code":"FM","supports_region":false,"supports_city":false},{"key":"FO","name":"Faroe Islands","type":"country","country_code":"FO","supports_region":true,"supports_city":false},{"key":"FR","name":"France","type":"country","country_code":"FR","supports_region":true,"supports_city":true},{"key":"GA","name":"Gabon","type":"country","country_code":"GA","supports_region":true,"supports_city":false},{"key":"GB","name":"United Kingdom","type":"country","country_code":"GB","supports_region":true,"supports_city":true},{"key":"GD","name":"Grenada","type":"country","country_code":"GD","supports_region":true,"supports_city":false},{"key":"GE","name":"Georgia","type":"country","country_code":"GE","supports_region":true,"supports_city":false},{"key":"GF","name":"French Guiana","type":"country","country_code":"GF","supports_region":false,"supports_city":false},{"key":"GG","name":"Guernsey","type":"country","country_code":"GG","supports_region":true,"supports_city":false},{"key":"GH","name":"Ghana","type":"country","country_code":"GH","supports_region":true,"supports_city":false},{"key":"GI","name":"Gibraltar","type":"country","country_code":"GI","supports_region":false,"supports_city":false},{"key":"GL","name":"Greenland","type":"country","country_code":"GL","supports_region":true,"supports_city":false},{"key":"GM","name":"The Gambia","type":"country","country_code":"GM","supports_region":true,"supports_city":false},{"key":"GN","name":"Guinea","type":"country","country_code":"GN","supports_region":true,"supports_city":false},{"key":"GP","name":"Guadeloupe","type":"country","country_code":"GP","supports_region":false,"supports_city":false},{"key":"GQ","name":"Equatorial Guinea","type":"country","country_code":"GQ","supports_region":true,"supports_city":false},{"key":"GR","name":"Greece","type":"country","country_code":"GR","supports_region":true,"supports_city":true},{"key":"GS","name":"South Georgia and the South Sandwich Islands","type":"country","country_code":"GS","supports_region":false,"supports_city":false},{"key":"GT","name":"Guatemala","type":"country","country_code":"GT","supports_region":true,"supports_city":true},{"key":"GU","name":"Guam","type":"country","country_code":"GU","supports_region":false,"supports_city":false},{"key":"GW","name":"Guinea-Bissau","type":"country","country_code":"GW","supports_region":true,"supports_city":false},{"key":"GY","name":"Guyana","type":"country","country_code":"GY","supports_region":true,"supports_city":false},{"key":"HK","name":"Hong Kong","type":"country","country_code":"HK","supports_region":true,"supports_city":false},{"key":"HM","name":"Heard Island and McDonald Islands","type":"country","country_code":"HM","supports_region":false,"supports_city":false},{"key":"HN","name":"Honduras","type":"country","country_code":"HN","supports_region":true,"supports_city":true},{"key":"HR","name":"Croatia","type":"country","country_code":"HR","supports_region":true,"supports_city":false},{"key":"HT","name":"Haiti","type":"country","country_code":"HT","supports_region":true,"supports_city":false},{"key":"HU","name":"Hungary","type":"country","country_code":"HU","supports_region":true,"supports_city":true},{"key":"ID","name":"Indonesia","type":"country","country_code":"ID","supports_region":true,"supports_city":true},{"key":"IE","name":"Ireland","type":"country","country_code":"IE","supports_region":true,"supports_city":true},{"key":"IL","name":"Israel","type":"country","country_code":"IL","supports_region":true,"supports_city":true},{"key":"IM","name":"Isle Of Man","type":"country","country_code":"IM","supports_region":false,"supports_city":false},{"key":"IN","name":"India","type":"country","country_code":"IN","supports_region":true,"supports_city":true},{"key":"IO","name":"British Indian Ocean Territory","type":"country","country_code":"IO","supports_region":false,"supports_city":false},{"key":"IQ","name":"Iraq","type":"country","country_code":"IQ","supports_region":true,"supports_city":false},{"key":"IS","name":"Iceland","type":"country","country_code":"IS","supports_region":true,"supports_city":false},{"key":"IT","name":"Italy","type":"country","country_code":"IT","supports_region":true,"supports_city":true},{"key":"JE","name":"Jersey","type":"country","country_code":"JE","supports_region":true,"supports_city":false},{"key":"JM","name":"Jamaica","type":"country","country_code":"JM","supports_region":true,"supports_city":false},{"key":"JO","name":"Jordan","type":"country","country_code":"JO","supports_region":true,"supports_city":false},{"key":"JP","name":"Japan","type":"country","country_code":"JP","supports_region":true,"supports_city":true},{"key":"KE","name":"Kenya","type":"country","country_code":"KE","supports_region":true,"supports_city":false},{"key":"KG","name":"Kyrgyzstan","type":"country","country_code":"KG","supports_region":true,"supports_city":false},{"key":"KH","name":"Cambodia","type":"country","country_code":"KH","supports_region":true,"supports_city":false},{"key":"KI","name":"Kiribati","type":"country","country_code":"KI","supports_region":false,"supports_city":false},{"key":"KM","name":"Comoros","type":"country","country_code":"KM","supports_region":true,"supports_city":false},{"key":"KN","name":"Saint Kitts and Nevis","type":"country","country_code":"KN","supports_region":false,"supports_city":false},{"key":"KP","name":"North Korea","type":"country","country_code":"KP","supports_region":false,"supports_city":false},{"key":"KR","name":"South Korea","type":"country","country_code":"KR","supports_region":true,"supports_city":true},{"key":"KW","name":"Kuwait","type":"country","country_code":"KW","supports_region":true,"supports_city":false},{"key":"KY","name":"Cayman Islands","type":"country","country_code":"KY","supports_region":true,"supports_city":false},{"key":"KZ","name":"Kazakhstan","type":"country","country_code":"KZ","supports_region":true,"supports_city":false},{"key":"LA","name":"Laos","type":"country","country_code":"LA","supports_region":true,"supports_city":false},{"key":"LB","name":"Lebanon","type":"country","country_code":"LB","supports_region":true,"supports_city":false},{"key":"LC","name":"St. Lucia","type":"country","country_code":"LC","supports_region":false,"supports_city":false},{"key":"LI","name":"Liechtenstein","type":"country","country_code":"LI","supports_region":true,"supports_city":false},{"key":"LK","name":"Sri Lanka","type":"country","country_code":"LK","supports_region":true,"supports_city":false},{"key":"LR","name":"Liberia","type":"country","country_code":"LR","supports_region":true,"supports_city":false},{"key":"LS","name":"Lesotho","type":"country","country_code":"LS","supports_region":true,"supports_city":false},{"key":"LT","name":"Lithuania","type":"country","country_code":"LT","supports_region":true,"supports_city":false},{"key":"LU","name":"Luxembourg","type":"country","country_code":"LU","supports_region":true,"supports_city":false},{"key":"LV","name":"Latvia","type":"country","country_code":"LV","supports_region":true,"supports_city":false},{"key":"LY","name":"Libya","type":"country","country_code":"LY","supports_region":true,"supports_city":false},{"key":"MA","name":"Morocco","type":"country","country_code":"MA","supports_region":true,"supports_city":false},{"key":"MC","name":"Monaco","type":"country","country_code":"MC","supports_region":true,"supports_city":false},{"key":"MD","name":"Moldova","type":"country","country_code":"MD","supports_region":true,"supports_city":false},{"key":"ME","name":"Montenegro","type":"country","country_code":"ME","supports_region":true,"supports_city":false},{"key":"MF","name":"Saint Martin","type":"country","country_code":"MF","supports_region":false,"supports_city":false},{"key":"MG","name":"Madagascar","type":"country","country_code":"MG","supports_region":true,"supports_city":false},{"key":"MH","name":"Marshall Islands","type":"country","country_code":"MH","supports_region":true,"supports_city":false},{"key":"MK","name":"Macedonia","type":"country","country_code":"MK","supports_region":true,"supports_city":false},{"key":"ML","name":"Mali","type":"country","country_code":"ML","supports_region":true,"supports_city":false},{"key":"MM","name":"Myanmar","type":"country","country_code":"MM","supports_region":false,"supports_city":false},{"key":"MN","name":"Mongolia","type":"country","country_code":"MN","supports_region":true,"supports_city":false},{"key":"MO","name":"Macau","type":"country","country_code":"MO","supports_region":false,"supports_city":false},{"key":"MP","name":"Northern Mariana Islands","type":"country","country_code":"MP","supports_region":false,"supports_city":false},{"key":"MQ","name":"Martinique","type":"country","country_code":"MQ","supports_region":false,"supports_city":false},{"key":"MR","name":"Mauritania","type":"country","country_code":"MR","supports_region":true,"supports_city":false},{"key":"MS","name":"Montserrat","type":"country","country_code":"MS","supports_region":false,"supports_city":false},{"key":"MT","name":"Malta","type":"country","country_code":"MT","supports_region":true,"supports_city":false},{"key":"MU","name":"Mauritius","type":"country","country_code":"MU","supports_region":true,"supports_city":false},{"key":"MV","name":"Maldives","type":"country","country_code":"MV","supports_region":true,"supports_city":false},{"key":"MW","name":"Malawi","type":"country","country_code":"MW","supports_region":true,"supports_city":false},{"key":"MX","name":"Mexico","type":"country","country_code":"MX","supports_region":true,"supports_city":true},{"key":"MY","name":"Malaysia","type":"country","country_code":"MY","supports_region":true,"supports_city":true},{"key":"MZ","name":"Mozambique","type":"country","country_code":"MZ","supports_region":true,"supports_city":false},{"key":"NA","name":"Namibia","type":"country","country_code":"NA","supports_region":true,"supports_city":false},{"key":"NC","name":"New Caledonia","type":"country","country_code":"NC","supports_region":true,"supports_city":false},{"key":"NE","name":"Niger","type":"country","country_code":"NE","supports_region":true,"supports_city":false},{"key":"NF","name":"Norfolk Island","type":"country","country_code":"NF","supports_region":false,"supports_city":false},{"key":"NG","name":"Nigeria","type":"country","country_code":"NG","supports_region":true,"supports_city":true},{"key":"NI","name":"Nicaragua","type":"country","country_code":"NI","supports_region":true,"supports_city":true},{"key":"NL","name":"Netherlands","type":"country","country_code":"NL","supports_region":true,"supports_city":true},{"key":"NO","name":"Norway","type":"country","country_code":"NO","supports_region":true,"supports_city":true},{"key":"NP","name":"Nepal","type":"country","country_code":"NP","supports_region":true,"supports_city":false},{"key":"NR","name":"Nauru","type":"country","country_code":"NR","supports_region":true,"supports_city":false},{"key":"NU","name":"Niue","type":"country","country_code":"NU","supports_region":false,"supports_city":false},{"key":"NZ","name":"New Zealand","type":"country","country_code":"NZ","supports_region":true,"supports_city":true},{"key":"OM","name":"Oman","type":"country","country_code":"OM","supports_region":true,"supports_city":false},{"key":"PA","name":"Panama","type":"country","country_code":"PA","supports_region":true,"supports_city":true},{"key":"PE","name":"Peru","type":"country","country_code":"PE","supports_region":true,"supports_city":true},{"key":"PF","name":"French Polynesia","type":"country","country_code":"PF","supports_region":true,"supports_city":false},{"key":"PG","name":"Papua New Guinea","type":"country","country_code":"PG","supports_region":true,"supports_city":false},{"key":"PH","name":"Philippines","type":"country","country_code":"PH","supports_region":true,"supports_city":true},{"key":"PK","name":"Pakistan","type":"country","country_code":"PK","supports_region":true,"supports_city":false},{"key":"PL","name":"Poland","type":"country","country_code":"PL","supports_region":true,"supports_city":true},{"key":"PM","name":"Saint Pierre and Miquelon","type":"country","country_code":"PM","supports_region":false,"supports_city":false},{"key":"PN","name":"Pitcairn","type":"country","country_code":"PN","supports_region":false,"supports_city":false},{"key":"PR","name":"Puerto Rico","type":"country","country_code":"PR","supports_region":true,"supports_city":true},{"key":"PS","name":"Palestine","type":"country","country_code":"PS","supports_region":false,"supports_city":false},{"key":"PT","name":"Portugal","type":"country","country_code":"PT","supports_region":true,"supports_city":true},{"key":"PW","name":"Palau","type":"country","country_code":"PW","supports_region":false,"supports_city":false},{"key":"PY","name":"Paraguay","type":"country","country_code":"PY","supports_region":true,"supports_city":true},{"key":"QA","name":"Qatar","type":"country","country_code":"QA","supports_region":true,"supports_city":false},{"key":"RE","name":"Réunion","type":"country","country_code":"RE","supports_region":false,"supports_city":false},{"key":"RO","name":"Romania","type":"country","country_code":"RO","supports_region":true,"supports_city":true},{"key":"RS","name":"Serbia","type":"country","country_code":"RS","supports_region":false,"supports_city":true},{"key":"RU","name":"Russia","type":"country","country_code":"RU","supports_region":true,"supports_city":true},{"key":"RW","name":"Rwanda","type":"country","country_code":"RW","supports_region":true,"supports_city":false},{"key":"SA","name":"Saudi Arabia","type":"country","country_code":"SA","supports_region":true,"supports_city":true},{"key":"SB","name":"Solomon Islands","type":"country","country_code":"SB","supports_region":true,"supports_city":false},{"key":"SC","name":"Seychelles","type":"country","country_code":"SC","supports_region":true,"supports_city":false},{"key":"SE","name":"Sweden","type":"country","country_code":"SE","supports_region":true,"supports_city":true},{"key":"SG","name":"Singapore","type":"country","country_code":"SG","supports_region":true,"supports_city":false},{"key":"SH","name":"Saint Helena","type":"country","country_code":"SH","supports_region":true,"supports_city":false},{"key":"SI","name":"Slovenia","type":"country","country_code":"SI","supports_region":true,"supports_city":false},{"key":"SJ","name":"Svalbard and Jan Mayen","type":"country","country_code":"SJ","supports_region":false,"supports_city":false},{"key":"SK","name":"Slovakia","type":"country","country_code":"SK","supports_region":true,"supports_city":false},{"key":"SL","name":"Sierra Leone","type":"country","country_code":"SL","supports_region":true,"supports_city":false},{"key":"SM","name":"San Marino","type":"country","country_code":"SM","supports_region":true,"supports_city":false},{"key":"SN","name":"Senegal","type":"country","country_code":"SN","supports_region":true,"supports_city":false},{"key":"SO","name":"Somalia","type":"country","country_code":"SO","supports_region":true,"supports_city":false},{"key":"SR","name":"Suriname","type":"country","country_code":"SR","supports_region":true,"supports_city":false},{"key":"SS","name":"South Sudan","type":"country","country_code":"SS","supports_region":true,"supports_city":false},{"key":"ST","name":"Sao Tome and Principe","type":"country","country_code":"ST","supports_region":true,"supports_city":false},{"key":"SV","name":"El Salvador","type":"country","country_code":"SV","supports_region":true,"supports_city":true},{"key":"SX","name":"Sint Maarten","type":"country","country_code":"SX","supports_region":false,"supports_city":false},{"key":"SZ","name":"Swaziland","type":"country","country_code":"SZ","supports_region":true,"supports_city":false},{"key":"TC","name":"Turks and Caicos Islands","type":"country","country_code":"TC","supports_region":false,"supports_city":false},{"key":"TD","name":"Chad","type":"country","country_code":"TD","supports_region":true,"supports_city":false},{"key":"TF","name":"French Southern Territories","type":"country","country_code":"TF","supports_region":false,"supports_city":false},{"key":"TG","name":"Togo","type":"country","country_code":"TG","supports_region":true,"supports_city":false},{"key":"TH","name":"Thailand","type":"country","country_code":"TH","supports_region":true,"supports_city":true},{"key":"TJ","name":"Tajikistan","type":"country","country_code":"TJ","supports_region":true,"supports_city":false},{"key":"TK","name":"Tokelau","type":"country","country_code":"TK","supports_region":true,"supports_city":false},{"key":"TL","name":"Timor-Leste","type":"country","country_code":"TL","supports_region":false,"supports_city":false},{"key":"TM","name":"Turkmenistan","type":"country","country_code":"TM","supports_region":true,"supports_city":false},{"key":"TN","name":"Tunisia","type":"country","country_code":"TN","supports_region":true,"supports_city":false},{"key":"TO","name":"Tonga","type":"country","country_code":"TO","supports_region":true,"supports_city":false},{"key":"TR","name":"Turkey","type":"country","country_code":"TR","supports_region":true,"supports_city":true},{"key":"TT","name":"Trinidad and Tobago","type":"country","country_code":"TT","supports_region":true,"supports_city":false},{"key":"TV","name":"Tuvalu","type":"country","country_code":"TV","supports_region":true,"supports_city":false},{"key":"TW","name":"Taiwan","type":"country","country_code":"TW","supports_region":true,"supports_city":true},{"key":"TZ","name":"Tanzania","type":"country","country_code":"TZ","supports_region":false,"supports_city":false},{"key":"UA","name":"Ukraine","type":"country","country_code":"UA","supports_region":true,"supports_city":true},{"key":"UG","name":"Uganda","type":"country","country_code":"UG","supports_region":true,"supports_city":false},{"key":"UM","name":"United States Minor Outlying Islands","type":"country","country_code":"UM","supports_region":false,"supports_city":false},{"key":"US","name":"United States","type":"country","country_code":"US","supports_region":true,"supports_city":true},{"key":"UY","name":"Uruguay","type":"country","country_code":"UY","supports_region":true,"supports_city":true},{"key":"UZ","name":"Uzbekistan","type":"country","country_code":"UZ","supports_region":true,"supports_city":false},{"key":"VA","name":"Vatican City","type":"country","country_code":"VA","supports_region":false,"supports_city":false},{"key":"VC","name":"Saint Vincent and the Grenadines","type":"country","country_code":"VC","supports_region":false,"supports_city":false},{"key":"VE","name":"Venezuela","type":"country","country_code":"VE","supports_region":true,"supports_city":true},{"key":"VG","name":"British Virgin Islands","type":"country","country_code":"VG","supports_region":false,"supports_city":false},{"key":"VI","name":"US Virgin Islands","type":"country","country_code":"VI","supports_region":false,"supports_city":false},{"key":"VN","name":"Vietnam","type":"country","country_code":"VN","supports_region":true,"supports_city":true},{"key":"VU","name":"Vanuatu","type":"country","country_code":"VU","supports_region":true,"supports_city":false},{"key":"WF","name":"Wallis and Futuna","type":"country","country_code":"WF","supports_region":false,"supports_city":false},{"key":"WS","name":"Samoa","type":"country","country_code":"WS","supports_region":false,"supports_city":false},{"key":"XK","name":"Kosovo","type":"country","country_code":"XK","supports_region":false,"supports_city":false},{"key":"YE","name":"Yemen","type":"country","country_code":"YE","supports_region":true,"supports_city":false},{"key":"YT","name":"Mayotte","type":"country","country_code":"YT","supports_region":false,"supports_city":false},{"key":"ZA","name":"South Africa","type":"country","country_code":"ZA","supports_region":true,"supports_city":true},{"key":"ZM","name":"Zambia","type":"country","country_code":"ZM","supports_region":true,"supports_city":false},{"key":"ZW","name":"Zimbabwe","type":"country","country_code":"ZW","supports_region":true,"supports_city":false}];
var countryMapFunction = function () {
    var map = {};
    for(var i = 0;i<regionList.length;i++){
        var item = regionList[i];
        var key = item["country_code"];
        map[key] = item["name"];
    }
    return map;
}
var countryMap = countryMapFunction();
var countryNames = [];
for (var i = 0; i < regionList.length; i++) {
    countryNames.push(regionList[i].name);
}

var campaignId;
var countryRevenueSpendReturn = "false";
$("#new_campaign_dlg .btn-primary").click(function() {
    $("#new_campaign_dlg").modal("hide");
    var campaignName = $('#inputCampaignName').val();
    var status = $('#inputStatus').prop('checked') ? 'ACTIVE' : 'PAUSED';
    var budget = $('#inputBudget').val();
    var bidding = $('#inputBidding').val();

    var tags = $('#inputTags').val();

    $.post('campaign/update', {
        campaignId: campaignId,
        campaignName: campaignName,
        status: status,
        budget: budget,
        bidding: bidding
    }, function (data) {
        if (data && data.ret == 1) {
//                $("#new_campaign_dlg").modal("hide");
            $('#btnSearch').click();
        } else {
            admanager.showCommonDlg("错误", data.message);
        }
    }, 'json');
});

var appQueryData = [];
var strFullPath = "";
function init() {

    var now = new Date(new Date().getTime() - 86400 * 1000);
    $('#inputStartTime').val(now.getFullYear() + "-" + (now.getMonth() + 1) + "-" + now.getDate());
    $('#inputEndTime').val(now.getFullYear() + "-" + (now.getMonth() + 1) + "-" + now.getDate());
    $('#inputStartTime').datetimepicker({
        minView: "month",
        format: 'yyyy-mm-dd',
        autoclose: true,
        todayBtn: true
    });
    $('#inputEndTime').datetimepicker({
        minView: "month",
        format: 'yyyy-mm-dd',
        autoclose: true,
        todayBtn: true
    });

    $("#inputCountry").autocomplete({
        source: countryNames
    });

    $("input:checkbox").on('click', function() {
        var $box = $(this);
        if ($box.is(":checked")) {
            var group = "input:checkbox[name='" + $box.attr("name") + "']";
            $(group).prop("checked", false);
            $box.prop("checked", true);
        } else {
            $box.prop("checked", false);
        }
    });


    $('#btnSearch').click(function () {
        var query = $("#inputSearch").val();
        var startTime = $('#inputStartTime').val();
        var endTime = $('#inputEndTime').val();

        var countryCode = '';
        var adwordsCheck = $('#adwordsCheck').is(':checked');
        var countryCheck = $('#countryCheck').is(':checked');
        var facebookCheck = $('#facebookCheck').is(':checked');

        var countryName = $('#inputCountry').val();
        for (var i = 0; i < regionList.length; i++) {
            if (countryName == regionList[i].name) {
                countryCode = regionList[i].country_code;
                break;
            }
        }



        $.post('query', {
            tag: query,
            startTime: startTime,
            endTime: endTime,
            adwordsCheck: adwordsCheck,
            countryCheck: countryCheck,
            facebookCheck: facebookCheck,
        }, function (data) {
            if (data && data.ret == 1) {
                appQueryData = data.data.array;
                $.post('query', {
                    tag: query,
                    startTime: startTime,
                    endTime: endTime,
                    adwordsCheck: adwordsCheck,
                    countryCheck: countryCheck,
                    facebookCheck: facebookCheck,
                    countryCode: countryCode,
                }, function (data) {
                    if (data && data.ret == 1) {
                        if (countryCheck) {
                            $('#result_header').html("<tr><th>国家</th><th>总展示</th><th>总花费</th><th>总安装</th><th>总点击</th><th>CPA</th><th>CTR</th><th>CVR</th></tr>");
                        } else if (adwordsCheck) {
                            $('#result_header').html("<tr><th>系列ID</th><th>广告账号ID</th><th>系列名称</th><th>创建时间<span sorterId=\"1\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>状态<span sorterId=\"2\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>预算<span sorterId=\"3\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>竞价<span sorterId=\"4\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>总花费<span sorterId=\"5\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>总安装<span sorterId=\"6\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>总点击<span sorterId=\"7\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>CPA<span sorterId=\"8\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>CTR<span sorterId=\"9\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>CVR<span sorterId=\"10\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th></tr>");
                        } else {
                            $('#result_header').html("<tr><th>系列ID</th><th>广告账号ID</th><th>系列名称</th><th>创建时间<span sorterId=\"1\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>状态<span sorterId=\"2\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>预算<span sorterId=\"3\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>竞价<span sorterId=\"4\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>总花费<span sorterId=\"5\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>总安装<span sorterId=\"6\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>总点击<span sorterId=\"7\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>CPA<span sorterId=\"8\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>CTR<span sorterId=\"9\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>CVR<span sorterId=\"10\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th></tr>");
                        }
                        data = data.data;
                        setData(data);
                        bindSortOp();
                        var str = "总花费: " + data.total_spend + " 总安装: " + data.total_installed +
                            " 总展示: " + data.total_impressions + " 总点击: " + data.total_click +
                            " CTR: " + data.total_ctr + " CPA: " + data.total_cpa + " CVR: " + data.total_cvr;
                        str += "<br/><span class='estimateResult'></span>"
                        $('#total_result').html(str);
                        $('#total_result').removeClass("editable");
                    } else {
                        admanager.showCommonDlg("错误", data.message);
                    }
                }, 'json');
            } else {
                admanager.showCommonDlg("错误", data.message);
            }
        }, 'json');
    });
    strFullPath = window.document.location.href;
    if(strFullPath.indexOf("?") != -1){
        countryRevenueSpendReturn = "true";
        strFullPath = strFullPath.substr(strFullPath.indexOf("?")+1,strFullPath.length);
        var thisArr = strFullPath.split("&");

        //顺序：tag_name startTime endTime country_code
        $("#inputSearch").val(thisArr[0].split("=")[1]);
        $('#inputStartTime').val(thisArr[1].split("=")[1]);
        $('#inputEndTime').val(thisArr[2].split("=")[1]);
        var country_code = thisArr[3].split("=")[1];
        $('#inputCountry').val(countryMap[country_code]);
        $('#btnSearch').click();
    }


    $('#btnSummary').click(function () {
        var query = $("#inputSearch").val();
        var startTime = $('#inputStartTime').val();
        var endTime = $('#inputEndTime').val();
        var adwordsCheck = $('#adwordsCheck').is(':checked');
        var facebookCheck = $('#facebookCheck').is(':checked');

        $.post('query', {
            tag: query,
            summary: true,
            startTime: startTime,
            endTime: endTime,
            adwordsCheck: adwordsCheck,
            facebookCheck: facebookCheck,
        }, function (data) {
            if (data && data.ret == 1) {
                $('#result_header').html("<tr><th>应用名称</th><th>总花费</th><th>总安装</th><th>总展示</th><th>总点击</th><th>CTR</th><th>CPA</th><th>CVR</th></tr>");
                data = data.data;

                var total_spend = 0;
                var total_installed = 0;
                var total_impressions = 0;
                var total_click = 0;
                var total_ctr = 0;
                var total_cpa = 0;
                var total_cvr = 0;

                $('#results_body > tr').remove();
                for (var i = 0; i < data.length; i++) {
                    var one = data[i];
                    var tr = $('<tr></tr>');
                    var keyset = ["name", "total_spend", "total_installed", "total_impressions", "total_click",
                        "total_ctr", "total_cpa", "total_cvr"];
                    for (var j = 0; j < keyset.length; j++) {
                        var td = $('<td></td>');
                        td.text(one[keyset[j]]);
                        tr.append(td);
                    }
                    total_spend += one['total_spend'];
                    total_installed += one['total_installed'];
                    total_impressions += one['total_impressions'];
                    total_click += one['total_click'];

                    total_ctr = total_impressions > 0 ? total_click / total_impressions : 0;
                    total_cpa = total_installed > 0 ? total_spend / total_installed : 0;
                    total_cvr = total_click > 0 ? total_installed / total_click : 0;
                    $('#results_body').append(tr);
                }
                var str = "总花费: " + total_spend + " 总安装: " + total_installed +
                    " 总展示: " + total_impressions + " 总点击: " + total_click +
                    " CTR: " + total_ctr + " CPA: " + total_cpa + " CVR: " + total_cvr;
                $('#total_result').text(str);
                $('#total_result').removeClass("editable");
            } else {
                admanager.showCommonDlg("错误", data.message);
            }
        }, 'json');
    });
}

function setData(data) {
    $('#results_body > tr').remove();
    for (var i = 0; i < data.array.length; i++) {
        var one = data.array[i];
        var tr = $('<tr></tr>');
        var countryCheck = $('#countryCheck').is(':checked');
        var keyset = ["campaign_id", "account_id", "campaign_name", "create_time",
            "status", "budget", "bidding", "spend", "installed", "click", "cpa", "ctr", "cvr"];
        var modifyColumns = ["campaign_name", "budget", "bidding"];
        if (countryCheck) {
            keyset = ["country_name",
                "impressions","spend", "installed", "click", "cpa", "ctr", "cvr"];
        }
        for (var j = 0; j < keyset.length; j++) {
            var campaignId = one['campaign_id'];
            var totalSpend = 0;
            for (var jj = 0; jj < appQueryData.length; jj++) {
                if (appQueryData[jj]['campaign_id'] == campaignId) {
                    totalSpend = appQueryData[jj]['spend'];
                    break;
                }
            }
            var td = $('<td></td>');
            if (keyset[j] == 'budget' || keyset[j] == 'bidding') {
                td.text(one[keyset[j]] / 100);
            } else {
                if (keyset[j] == 'spend') {
                    td.text(one[keyset[j]] + " / " + totalSpend);
                } else {
                    td.text(one[keyset[j]]);
                }
            }
            if (modifyColumns.indexOf(keyset[j]) != -1) {
                td.addClass("editable");
                td[0].cloumnName = keyset[j];
            }
            tr.append(td);
        }
        tr[0].origCampaignData = one;
        tr[0].changedCampainData = {};
        var admobCheck = $('#admobCheck').is(':checked');
        var countryCheck = $('#countryCheck').is(':checked');
        if (!admobCheck && !countryCheck) {
//                var td = $('<td><a class="link_modify" href="javascript:void(0)">修改</a><a class="link_copy" href="javascript:void(0)">复制</a></td>');
//                tr.append(td);
        }
        $('#results_body').append(tr);
    }
    bindOp();
}

function bindOp() {
    $(".link_modify").click(function() {
        $('#modify_form').show();

        $("#dlg_title").text("修改系列");

        var tds = $(this).parents("tr").find('td');
        campaignId = $(tds.get(0)).text();
        var campaignName = $(tds.get(2)).text();
        var status = $(tds.get(4)).text();
        var budget = $(tds.get(5)).text();
        var bidding = $(tds.get(6)).text();

        $('#inputCampaignName').val(campaignName);
        if (status.toLowerCase() == 'active') {
            $('#inputStatus').prop('checked', true);
        } else {
            $('#inputStatus').prop('checked', false);
        }
        $('#inputBudget').val(budget);
        $('#inputBidding').val(bidding);

        $("#new_campaign_dlg").modal("show");
    });

    $(".link_copy").click(function() {
        var tds = $(this).parents("tr").find('td');
        $.post('campaign/find_create_data', {
            campaignId: $(tds.get(0)).text(),
        }, function (data) {
            if (data && data.ret == 1) {
                var list = [];
                var keys = ["tag_name", "app_name", "facebook_app_id", "account_id", "country_region",
                    "language","age", "gender", "detail_target", "campaign_name", "page_id", "bugdet", "bidding", "max_cpa", "title", "message"];
                var data = data.data;
                for (var i = 0; i < keys.length; i++) {
                    list.push(data[keys[i]]);
                }
                admanager.showCommonDlg("请手动创建", list.join("\t"));
            } else {
                admanager.showCommonDlg("错误", data.message);
            }
        }, 'json');
    });
}

function bindSortOp() {
    $('.sorter').click(function() {
        var sorterId = $(this).attr('sorterId');
        sorterId = parseInt(sorterId);
        if ($(this).hasClass("glyphicon-arrow-up")) {
            $(this).removeClass("glyphicon-arrow-up");
            $(this).addClass("glyphicon-arrow-down");
            sorterId += 1000;
        } else {
            $(this).removeClass("glyphicon-arrow-down");
            $(this).addClass("glyphicon-arrow-up");
        }

        var query = $("#inputSearch").val();
        var startTime = $('#inputStartTime').val();
        var endTime = $('#inputEndTime').val();
        var countryName = $('#inputCountry').val();
        var countryCode = '';
        var adwordsCheck = $('#adwordsCheck').is(':checked');
        var countryCheck = $('#countryCheck').is(':checked');
        var facebookCheck = $('#facebookCheck').is(':checked');
        for (var i = 0; i < regionList.length; i++) {
            if (countryName == regionList[i].name) {
                countryCode = regionList[i].country_code;
                break;
            }
        }

        $.post('query', {
            tag: query,
            startTime: startTime,
            endTime: endTime,
            adwordsCheck: adwordsCheck,
            countryCheck: countryCheck,
            facebookCheck: facebookCheck,
        }, function (data) {
            if (data && data.ret == 1) {
                appQueryData = data.data.array;
                $.post('query', {
                    tag: query,
                    startTime: startTime,
                    endTime: endTime,
                    adwordsCheck: adwordsCheck,
                    countryCheck: countryCheck,
                    facebookCheck: facebookCheck,
                    countryCode: countryCode,
                    sorterId: sorterId
                }, function (data) {
                    if (data && data.ret == 1) {
                        data = data.data;
                        setData(data);
                        var str = "总花费: " + data.total_spend + " 总安装: " + data.total_installed +
                            " 总展示: " + data.total_impressions + " 总点击: " + data.total_click +
                            " CTR: " + data.total_ctr + " CPA: " + data.total_cpa + " CVR: " + data.total_cvr;

                        str += "<br/><span class='estimateResult'></span>"
                        $('#total_result').removeClass("editable");
                        $('#total_result').html(str);
                    } else {
                        admanager.showCommonDlg("错误", data.message);
                    }
                }, 'json');
            }
        }, 'json');
    });
}

function estimateCost() {
    var total_spend = 0;
    var total_installed = 0;
    var total_impressions = 0;
    var total_click = 0;
    var total_ctr = 0;
    var total_cpa = 0;
    var total_cvr = 0;

    $('#results_body tr').each(function() {
        if (!this.origCampaignData) return;
        total_spend += this.origCampaignData.spend;
        if (this.changedCampainData.enabled === false) {
            total_spend -= this.origCampaignData.spend;
        }
        total_installed += this.origCampaignData.installed;
        if (this.changedCampainData.enabled === false) {
            total_installed -= this.origCampaignData.installed;
        }
        total_impressions += this.origCampaignData.impressions;
        total_click += this.origCampaignData.click;
    });
    $('#results_body tr').each(function() {
        if (!this.changedCampainData) return;
        if (this.changedCampainData.budget > 0 && this.changedCampainData.budget != this.origCampaignData.budget) {
            total_spend += (this.changedCampainData.budget * 100 - this.origCampaignData.budget) / this.origCampaignData.budget * this.origCampaignData.spend;
            var count = (this.changedCampainData.budget * 100 - this.origCampaignData.budget) / this.origCampaignData.budget  * this.origCampaignData.installed;
            total_installed += parseInt(count);
        }
    });

    total_ctr = total_impressions > 0 ? total_click / total_impressions : 0;
    total_cpa = total_installed > 0 ? total_spend / total_installed : 0;
    total_cvr = total_click > 0 ? total_installed / total_click : 0;

    var str = "总花费: " + total_spend + " 总安装: " + total_installed +
        " 总展示: " + total_impressions + " 总点击: " + total_click +
        " CTR: " + total_ctr + " CPA: " + total_cpa + " CVR: " + total_cvr;

    $('.estimateResult').text(str);
}

function bindBatchModifyOperation() {
    $(document).click(function() {
        $('#results_body td.editable').removeClass('editing');
        $(".inputTemp").replaceWith(function() {
            var td = $(".inputTemp").parent('td')[0];
            var tr = $(td).parents('tr')[0];
            if (td.origValue != this.value) {
                switch (td.cloumnName) {
                    case 'campaign_name':
                        tr.changedCampainData.campaignName = this.value;
                        break;
                    case 'budget':
                        tr.changedCampainData.budget = this.value;
                        break;
                    case 'bidding':
                        tr.changedCampainData.bidding = this.value;
                        break;
                }
                $(td).addClass("changed");
            } else {
                switch (td.cloumnName) {
                    case 'campaign_name':
                        tr.changedCampainData.campaignName = null;
                        break;
                    case 'budget':
                        tr.changedCampainData.budget = null;
                        break;
                    case 'bidding':
                        tr.changedCampainData.bidding = null;
                        break;
                }
                $(td).removeClass("changed");
            }
            return this.value;
        });
        estimateCost();
    });

    $('#btnModifyBatch').click(function() {
        var checkbox = $('#result_header input[type=checkbox]');
        if (checkbox.length > 0) return;

        $('#result_header tr th:first').before($("<th><input id='ckSelectAll' type='checkbox' /></th>"));
        $('#results_body tr').each(function() {
            $(this).find('td:first').before($("<td><input class='ckEnableCampaign' type='checkbox' /></td>"));
            if (this.origCampaignData.network == 'admob') {
                if (this.origCampaignData.status == 'enabled') {
                    this.origCampaignData.campainEnabled = true;
                    $(this).find('input[type=checkbox]').prop('checked', true);
                } else {
                    this.origCampaignData.campainEnabled = false;
                    $(this).find('input[type=checkbox]').prop('checked', false);
                }
            } else {
                if (this.origCampaignData.status == 'ACTIVE') {
                    this.origCampaignData.campainEnabled = true;
                    $(this).find('input[type=checkbox]').prop('checked', true);
                } else {
                    this.origCampaignData.campainEnabled = false;
                    $(this).find('input[type=checkbox]').prop('checked', false);
                }
            }
        });
        $('.ckEnableCampaign').change(function() {
            var tr = $(this).parents('tr')[0];
            var data = tr.origCampaignData;
            if ($(this).is(':checked') != data.campainEnabled) {
                tr.changedCampainData.enabled = $(this).is(':checked');
                $(this).parents('td').addClass('changed');
            } else {
                $(this).parents('td').removeClass('changed');
                tr.changedCampainData.enabled = null;
            }
            estimateCost();
        });
        $('#ckSelectAll').change(function() {
            if ($(this).is(":checked")) {
                $('#results_body input[type=checkbox]').prop('checked', true);
            } else {
                $('#results_body input[type=checkbox]').prop('checked', false);
            }
            $('#results_body input[type=checkbox]').trigger('change');
        });

        $('#total_result').addClass("editable");

        $('#results_body td.editable').click(function (e) {
            e.stopPropagation();
            if ($(this).hasClass('editing')) {
                return;
            }
            $(this).addClass('editing');
            var value = $(this).text();
            updateVal(this, value);
        });

        function updateVal(currentEle, value) {
            if (!currentEle.origValue) {
                currentEle.origValue = value;
            }
            $(currentEle).html('<input class="inputTemp" type="text" width="2" value="' + value + '" />');
            $(".inputTemp", currentEle).focus()
                // .keyup(function (event) {
                //     if (event.keyCode == 13) {
                //         $(currentEle).removeClass('editing');
                //         $(currentEle).text($(".inputTemp").val().trim());
                //         if ($(currentEle).text() != currentEle.origValue) {
                //             $(currentEle).addClass("changed");
                //         } else {
                //             $(currentEle).removeClass("changed");
                //         }
                //     }
                // })
                .click(function(e) {
                e.stopPropagation();
            });
        }
    });

    $('#btnModifySubmit').click(function() {
        var list = [];
        var countryName = $('#inputCountry').val();
        var countryCode = '';
        for (var i = 0; i < regionList.length; i++) {
            if (countryName == regionList[i].name) {
                countryCode = regionList[i].country_code;
                break;
            }
        }
        if (countryCode == '' && countryName != '') {
            return;
        }

        $('#results_body td.changed').each(function() {
            var tr = $(this).parents('tr')[0];
            var one = tr.changedCampainData;
            if (countryCode != '') {
                if (one.enabled === false) {
                    one.excludedCountry = countryCode;
                    delete one.enabled;
                }
            }
            one.network = tr.origCampaignData.network;
            one.campaignId = tr.origCampaignData.campaign_id;
            one.accountId = tr.origCampaignData.account_id;
            for (var i = 0; i < list.length; i++) {
                if (list[i].campaignId == one.campaignId) {
                    return;
                }
            }
            list.push(one);
        });
        $.post('campaign/batch_change', {
            data: JSON.stringify(list),
        }, function (data) {
            if (data && data.ret == 1) {
                admanager.showCommonDlg("提示", "修改任务提交成功");
            } else {
                admanager.showCommonDlg("错误", data.message);
            }
        }, 'json');
    });
}

function bindQueryZero() {
    $('#btnCloseZero').click(function() {
        var yes = confirm("小心啊，确认关闭吗？");
        if (yes) {
            var startTime = $('#inputStartTime').val();
            var endTime = $('#inputEndTime').val();

            var costOp = $('#selectCostOp').val();
            var conversionOp = $('#selectConversionOp').val();
            var cost = $('#inputCostRate').val();
            var conversion = $('#inputConversion').val();

            $.post("query_zero/close", {
                startTime: startTime,
                endTime: endTime,
                costOp: costOp,
                conversionOp: conversionOp,
                cost: cost,
                conversion: conversion,
            }, function(data) {
                if (data && data.ret == 1) {
                    admanager.showCommonDlg("提示", "提交任务成功");
                }
            }, 'json');
        }
    });
    $('#btnQueryZero').click(function() {
        var startTime = $('#inputStartTime').val();
        var endTime = $('#inputEndTime').val();

        var costOp = $('#selectCostOp').val();
        var conversionOp = $('#selectConversionOp').val();
        var cost = $('#inputCostRate').val();
        var conversion = $('#inputConversion').val();

        $.post("query_zero/query", {
            startTime: startTime,
            endTime: endTime,
            costOp: costOp,
            conversionOp: conversionOp,
            cost: cost,
            conversion: conversion,
        }, function(data) {
            if (data && data.ret == 1) {
                $('#result_header').html("<tr><th>系列ID</th><th>广告账号ID</th><th>系列名称</th><th>创建时间<span sorterId=\"1\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>状态<span sorterId=\"2\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>预算<span sorterId=\"3\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>竞价<span sorterId=\"4\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>总花费<span sorterId=\"5\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>总安装<span sorterId=\"6\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>总点击<span sorterId=\"7\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>CPA<span sorterId=\"8\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>CTR<span sorterId=\"9\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th><th>CVR<span sorterId=\"10\" class=\"sorter glyphicon glyphicon-arrow-up\"></span></th></tr>");
                data = data.data;
                setData(data);
                bindSortOp();

                var now = new Date();
                var six = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 16, 0, 0);
                var estimatedCost = ((six - now) / 1000) * (data.total_spend / (86400 - (six - now) / 1000)) + data.total_spend;
                var str = "<span style='color:red;'>只算还在开启状态的系列</span>" + " 总预算: " + data.total_bugdet + " 总花费: " + data.total_spend + " <span style='color:red'>预计花费: " + estimatedCost + "</span> 总安装: " + data.total_installed +
                    " 总展示: " + data.total_impressions + " 总点击: " + data.total_click +
                    " CTR: " + data.total_ctr + " CPA: " + data.total_cpa + " CVR: " + data.total_cvr;
                str += "<br/><span class='estimateResult'></span>"
                $('#total_result').html(str);
                $('#total_result').removeClass("editable");
            } else {
                admanager.showCommonDlg("错误", data.message);
            }
        }, "json");
    });
}

init();
bindQueryZero();
bindBatchModifyOperation();

if(countryRevenueSpendReturn == "false"){
    $('#btnSummary').click();
}
