# DIGITALNI RASPORED 

## Opis Junit testova

###  NewCourseValidInputTest - sadrži testove za provjeru ispravnosti unosa prilikom dodavanja novog kolegija
* Test hour_isCorrect provjerava mogućnost upisivanja pozitivnog broja sati
* Test hour_isNegative provjerava mogućnost upisivanja negativnog broja sati 
* Test hour_isEmpty provjerava mogućnost ne upisivanja nikakve vrijednosti kod unosa broja sati

###  NewExamValidInputTest - sadrži testove za provjeru ispravnosti unosa prilikom dodavanja novog ispita
* Test date_isCorrect provjerava mogućnost upisivanja budućeg datuma
* Test date_isIncorrect provjerava mogućnost upisivanja prošlog datuma

###  NewFacultyValidInputTest - sadrži testove za provjeru ispravnosti unosa prilikom dodavanja novog fakulteta
* Test studyOfYear_isCorrect provjerava mogućnost upisivanja pozitivnog broja
* Test studyOfYear_isNegative provjerava mogućnost upisivanja negativnog broja
* Test studyOfYear_isZero provjerava mogućnost upisivanja nule
* Test studyOfYear_isEmpty provjerava mogućnost ne upisivanja nikakve vrijednosti kod godine studiranja
* Test studyOfYear_isTooHigh provjera mogućnosti upisivanja prevelikog broja

###  NewLectureValidInputTest - sadrži testove za provjeru ispravnosti unosa prilikom dodavanja novog predavanja
* Test date_isCorrect provjerava mogućnost upisivanja budućeg datuma
* Test date_isIncorrect provjerava mogućnost upisivanja prošlog datuma
* Test time_isCorrect provjerava mogućnost upisivanja budućeg vremena
* Test time_isIncorrect provjerava mogućnost upisivanja prošlog vremena
