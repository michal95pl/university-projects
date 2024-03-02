# GSM simulator
Użytkownik aplikacji tworzy pewną ilość obiektów VBD, z których każdy jest oddzielnym
wątkiem i natychmiast rozpoczyna transmitowanie wprowadzonej podczas tworzenia wiadomości.
Ponieważ zgodnie ze standardem, każdy SMS ma zakodowany numer nadawcy i
odbiorcy wraz z wiadomością, jako odbiorca wybierany jest losowy element VRD. Utworzony
SMS zostaje przekazany do stacji BTS, o najmniejszej liczbie oczekujących na
wysłanie SMSów.
Aplikacja w stanie uruchomieniowym zawiera trzy warstwy. Pomiędzy warstwą wejściową
a warstwą wyjściową musi się znajdować przynajmniej jedna warstwa pośrednia
kontrolerów BSC. Dokłada ilość warstw pośrednich jest zależna od akcji użytkownika aplikacji,
który za pomocą klawiszy może dodać lub usunąć warstwę. Każda nowo utworzona
warstwa komunikacyjna będzie tworzona z jednym BSC, natomiast usunięcie warstwy
skutkuje zaprzestaniem przyjmowania wiadomości przez tą warstwę i natychmiastowym
przekazaniem wiadomości z wszystkich BSC z pominięciem czasu przekazania.
Przekazywanie przykładowej wiadomości SMS będzie wyglądało następująco:
### VBD → BTS → BSC → · · · → BTS → VRD

<img width="665" alt="image" src="https://github.com/michal95pl/university-projects/assets/85219287/121f1ff5-dc9a-4ed4-8e8f-023cdc158e94">
