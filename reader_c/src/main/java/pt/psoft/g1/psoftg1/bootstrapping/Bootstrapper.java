package pt.psoft.g1.psoftg1.bootstrapping;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pt.psoft.g1.psoftg1.idgeneratormanagement.IdGenerator;
import pt.psoft.g1.psoftg1.lendingmanagement.repositories.LendingRepository;
import pt.psoft.g1.psoftg1.readermanagement.repositories.ReaderRepository;
import pt.psoft.g1.psoftg1.shared.services.ForbiddenNameService;

@Component
@RequiredArgsConstructor
@Profile("bootstrap")
@PropertySource({"classpath:config/library.properties"})
@Order(2)
public class Bootstrapper implements CommandLineRunner
{
    @Value("${lendingDurationInDays}")
    private int lendingDurationInDays;
    @Value("${fineValuePerDayInCents}")
    private int fineValuePerDayInCents;

    private final LendingRepository lendingRepository;
    private final ReaderRepository readerRepository;
    private final ForbiddenNameService forbiddenNameService;
    private final IdGenerator idGenerator;

    // @Autowired
    // private AuthNUsersEventsRabiitMQRPCAdapter rpc;

    @Override
    @Transactional
    public void run(final String... args)
    {
        // createReaders();
        // loadForbiddenNames();
        // createLendings();
        // createPhotos();
    }

    protected void loadForbiddenNames() {
        String fileName = "forbiddenNames.txt";
        forbiddenNameService.loadDataFromFile(fileName);
    }

    protected void createReaders()
    {
        // if (readerRepository.findByEmail("manuel@gmail.com").isEmpty())
        // {

        // }
    }
}

//     private final GenreRepository genreRepository;
//     private final BookRepository bookRepository;
//     private final AuthorRepository authorRepository;



//     private void createAuthors()
//     {
//         if (authorRepository.searchByNameName("Manuel Antonio Pina").isEmpty())
//         {
//             final Author author = new Author("Manuel Antonio Pina",
//                     "Manuel António Pina foi um jornalista e escritor português, premiado em 2011 com o Prémio Camões",
//                     null);
//             authorRepository.save(author);
//         }
//         if (authorRepository.searchByNameName("Antoine de Saint Exupery").isEmpty())
//         {
//             final Author author = new Author("Antoine de Saint Exupery",
//                     "Antoine de Saint-Exupery nasceu a 29 de junho de 1900 em Lyon. Faz o seu batismo de voo aos 12 anos, aos 22 torna-se piloto militar e é como capitão que em 1939 se junta à Força Aérea francesa em luta contra a ocupação nazi. A aviação e a guerra viriam a revelar-se elementos centrais de toda a sua obra literária, onde se destacam títulos como Correio do Sul (1929), o seu primeiro romance, Voo Noturno (1931), que logo se tornou um êxito de vendas internacional, e Piloto de Guerra (1942), retrato da sua participação na Segunda Guerra Mundial. Em 1943 publicaria aquela que é reconhecida como a sua obra-prima, O Principezinho, um dos livros mais traduzidos em todo o mundo. A sua morte, aos 44 anos, num acidente de aviação durante uma missão de reconhecimento no sul de França, permanece ainda hoje um mistério.",
//                     null);
//             authorRepository.save(author);
//         }
//         if (authorRepository.searchByNameName("Alexandre Pereira").isEmpty())
//         {
//             final Author author = new Author("Alexandre Pereira",
//                     "Alexandre Pereira é licenciado e mestre em Engenharia Electrotécnica e de Computadores, pelo Instituto Superior Técnico. É, também, licenciado em Antropologia, pela Faculdade de Ciências Sociais e Humanas da Universidade Nova de Lisboa.\n" +
//                             "É Professor Auxiliar Convidado na Universidade Lusófona de Humanidades e Tecnologias, desde Março de 1993, onde lecciona diversas disciplinas na Licenciatura de Informática e lecciona uma cadeira de introdução ao SPSS na Licenciatura de Psicologia.\n" +
//                             "Tem também leccionado cursos de formação na área da aplicação da informática ao cálculo estatístico e processamento de dados utilizando o SPSS, em diversas instituições, nomeadamente no Instituto Nacional de Estatística.\n" +
//                             "Para além disso, desenvolve aplicações informáticas na área da Psicologia Cognitiva, no âmbito de projectos de investigação do departamento de Psicologia Cognitiva da Faculdade de Psicologia da Universidade de Lisboa.\n" +
//                             "Está ainda ligado a projectos de ensino à distância desenvolvidos na Faculdade de Motricidade Humana da Universidade Técnica de Lisboa.\n" +
//                             "Paralelamente, tem desenvolvido aplicações de software comercial, área onde continua em actividade. ",
//                     null);
//             authorRepository.save(author);
//         }
//         if (authorRepository.searchByNameName("Filipe Portela").isEmpty())
//         {
//             final Author author = new Author("Filipe Portela",
//                     " «Docente convidado na Escola de Engenharia da Universidade do Minho. Investigador integrado do Centro Algoritmi. CEO e fundador da startup tecnológica IOTech - Innovation on Technology. Coautor do livro Introdução ao Desenvolvimento Moderno para a Web. ",
//                     null);
//             authorRepository.save(author);
//         }
//         if (authorRepository.searchByNameName("Ricardo Queiros").isEmpty())
//         {
//             final Author author = new Author("Ricardo Queiros",
//                     "Docente na Escola Superior de Media Artes e Design do Politécnico do Porto. Diretor da uniMAD (ESMAD) e membro efetivo do CRACS (INESC TEC). Autor de vários livros sobre tecnologias Web e programação móvel, publicados pela FCA. Coautor do livro Introdução ao Desenvolvimento Moderno para a Web.",
//                     null);
//             authorRepository.save(author);
//         }
//         if (authorRepository.searchByNameName("Freida Mcfadden").isEmpty())
//         {
//             final Author author = new Author("Freida Mcfadden",
//                     "Freida McFadden é médica e especialista em lesões cerebrais. Autora de diversos thrillers psicológicos, todos eles bestsellers, já traduzidos para mais de 30 idiomas. As suas obras foram selecionadas para «O Melhor Livro do Ano» na Amazon e também para «Melhor Thriller» dos Goodreads Choice Awards.\n" +
//                             "Freida vive com a sua família e o gato preto numa casa de três andares com vista para o oceano, com escadas que rangem e gemem a cada passo, e ninguém conseguiria ouvi-la se gritasse. A menos que gritasse muito alto, talvez.",
//                     null);
//             authorRepository.save(author);
//         }
//         if (authorRepository.searchByNameName("J R R Tolkien").isEmpty())
//         {
//             final Author author = new Author("J R R Tolkien",
//                     "J.R.R. Tolkien nasceu a 3 de Janeiro de 1892, em Bloemfontein.\n" +
//                             "Depois de ter combatido na Primeira Guerra Mundial, dedicou-se a uma ilustre carreira académica e foi reconhecido como um dos grandes filólogos do planeta.\n" +
//                             "Foi a criação da Terra Média, porém, a trazer-lhe a celebridade. Autor de extraordinários clássicos da ficção, de que são exemplo O Hobbit, O Senhor dos Anéis e O Silmarillion, os seus livros foram traduzidos em mais de 60 línguas e venderam largos milhões de exemplares no mundo inteiro.\n" +
//                             "Tolkien foi nomeado Comandante da Ordem do Império Britânico e, em 1972, foi-lhe atribuído o título de Doutor Honoris Causa, pela Universidade de Oxford.\n" +
//                             "Morreu em 1973, com 81 anos.",
//                     "authorPhotoTest.jpg");
//             authorRepository.save(author);
//         }
//         if (authorRepository.searchByNameName("Gardner Dozois").isEmpty())
//         {
//             final Author author = new Author("Gardner Dozois",
//                     "Gardner Raymond Dozois (23 de julho de 1947 – 27 de maio de 2018) foi um autor de ficção científica norte-americano.\n" +
//                             "Foi o fundador e editor do Melhores Do Ano de Ficção científica antologias (1984–2018) e foi editor da revista Asimov Ficção científica (1984-2004), ganhando vários prémios.",
//                     null);
//             authorRepository.save(author);
//         }
//         if (authorRepository.searchByNameName("Lisa Tuttle").isEmpty())
//         {
//             final Author author = new Author("Lisa Tuttle",
//                     "Lisa Gracia Tuttle (nascida a 16 de setembro de 1952) é uma autora americana de ficção científica, fantasia e terror. Publicou mais de uma dúzia de romances, sete coleções de contos e vários títulos de não-ficção, incluindo um livro de referência sobre feminismo, \"Enciclopédia do Feminismo\" (1986). Também editou várias antologias e fez críticas de livros para diversas publicações. Vive no Reino Unido desde 1981.\n" +
//                             "Tuttle ganhou o Prémio John W. Campbell para Melhor Novo Escritor em 1974, recebeu o Prémio Nebula de Melhor Conto em 1982 por \"The Bone Flute\", que recusou, e o Prémio BSFA de Ficção Curta em 1989 por \"In Translation\".",
//                     null);
//             authorRepository.save(author);
//         }
//     }


//     protected void loadForbiddenNames() {
//         String fileName = "forbiddenNames.txt";
//         forbiddenNameService.loadDataFromFile(fileName);
//     }

//     private void createLendings() {
//         int i;
//         int seq = 0;

//         final var book1 = bookRepository.findByIsbn("9789720706386");
//         final var book2 = bookRepository.findByIsbn("9789723716160");
//         final var book3 = bookRepository.findByIsbn("9789895612864");
//         final var book4 = bookRepository.findByIsbn("9782722203402");
//         final var book5 = bookRepository.findByIsbn("9789722328296");
//         final var book6 = bookRepository.findByIsbn("9789895702756");
//         final var book7 = bookRepository.findByIsbn("9789897776090");
//         final var book8 = bookRepository.findByIsbn("9789896379636");
//         final var book9 = bookRepository.findByIsbn("9789896378905");
//         final var book10 = bookRepository.findByIsbn("9789896375225");

//         List<Book> books = new ArrayList<>();

//         if(book1.isPresent() && book2.isPresent() && book3.isPresent() && book4.isPresent() && book5.isPresent() && book6.isPresent() && book7.isPresent() && book8.isPresent() && book9.isPresent() && book10.isPresent())
//         {
//             books = List.of(new Book[]{book1.get(), book2.get(), book3.get(), book4.get(), book5.get(), book6.get(), book7.get(), book8.get(), book9.get(), book10.get()});
//         }

//         final var readerDetails1 = readerRepository.findByReaderNumber("2025/1");
//         final var readerDetails2 = readerRepository.findByReaderNumber("2025/2");
//         final var readerDetails3 = readerRepository.findByReaderNumber("2025/3");
//         final var readerDetails4 = readerRepository.findByReaderNumber("2025/4");
//         final var readerDetails5 = readerRepository.findByReaderNumber("2025/5");
//         final var readerDetails6 = readerRepository.findByReaderNumber("2025/6");

//         List<ReaderDetails> readers = new ArrayList<>();

//         if(readerDetails1.isPresent() && readerDetails2.isPresent() && readerDetails3.isPresent() && readerDetails4.isPresent() && readerDetails5.isPresent() && readerDetails6.isPresent())
//         {
//             readers = List.of(new ReaderDetails[]{readerDetails1.get(), readerDetails2.get(), readerDetails3.get(), readerDetails4.get(), readerDetails5.get(), readerDetails6.get()});
//         }

//         LocalDate startDate;
//         LocalDate returnedDate;
//         Lending lending;

//         // safety check
//         if (books.isEmpty())
//         {
//             System.out.println("No books available to create lendings.");
//             return;
//         }
//         else if (readers.isEmpty())
//         {
//             System.out.println("No readers available to create lendings.");
//             return;
//         }

//         //Lendings 1 through 3 (late, returned)
//         for(i = 0; i < 3; i++){
//             ++seq;
//             if(lendingRepository.findByLendingNumber("2025/" + seq).isEmpty()){
//                 startDate = LocalDate.of(2025, 1,31-i);
//                 returnedDate = LocalDate.of(2025,2,15+i);
//                 lending = Lending.newBootstrappingLending(books.get(i), readers.get(i*2), 2025, seq, startDate, returnedDate, lendingDurationInDays, fineValuePerDayInCents);
//                 lending.setId(idGenerator.generateId());
//                 lendingRepository.save(lending);
//             }
//         }

//         //Lendings 4 through 6 (overdue, not returned)
//         for(i = 0; i < 3; i++){
//             ++seq;
//             if(lendingRepository.findByLendingNumber("2025/" + seq).isEmpty()){
//                 startDate = LocalDate.of(2025, 3,25+i);
//                 lending = Lending.newBootstrappingLending(books.get(1+i), readers.get(1+i*2), 2025, seq, startDate, null, lendingDurationInDays, fineValuePerDayInCents);
//                 lending.setId(idGenerator.generateId());
//                 lendingRepository.save(lending);
//             }
//         }
//         //Lendings 7 through 9 (late, overdue, not returned)
//         for(i = 0; i < 3; i++){
//             ++seq;
//             if(lendingRepository.findByLendingNumber("2025/" + seq).isEmpty()){
//                 startDate = LocalDate.of(2025, 4,(1+2*i));
//                 lending = Lending.newBootstrappingLending(books.get(3/(i+1)), readers.get(i*2), 2025, seq, startDate, null, lendingDurationInDays, fineValuePerDayInCents);
//                 lending.setId(idGenerator.generateId());
//                 lendingRepository.save(lending);
//             }
//         }

//         //Lendings 10 through 12 (returned)
//         for(i = 0; i < 3; i++){
//             ++seq;
//             if(lendingRepository.findByLendingNumber("2025/" + seq).isEmpty()){
//                 startDate = LocalDate.of(2025, 5,(i+1));
//                 returnedDate = LocalDate.of(2025,5,(i+2));
//                 lending = Lending.newBootstrappingLending(books.get(3-i), readers.get(1+i*2), 2025, seq, startDate, returnedDate, lendingDurationInDays, fineValuePerDayInCents);
//                 lending.setId(idGenerator.generateId());
//                 lendingRepository.save(lending);
//             }
//         }

//         //Lendings 13 through 18 (returned)
//         for(i = 0; i < 6; i++){
//             ++seq;
//             if(lendingRepository.findByLendingNumber("2025/" + seq).isEmpty()){
//                 startDate = LocalDate.of(2025, 5,(i+2));
//                 returnedDate = LocalDate.of(2025,5,(i+2*2));
//                 lending = Lending.newBootstrappingLending(books.get(i), readers.get(i), 2025, seq, startDate, returnedDate, lendingDurationInDays, fineValuePerDayInCents);
//                 lending.setId(idGenerator.generateId());
//                 lendingRepository.save(lending);
//             }
//         }

//         //Lendings 19 through 23 (returned)
//         for(i = 0; i < 6; i++){
//             ++seq;
//             if(lendingRepository.findByLendingNumber("2025/" + seq).isEmpty()){
//                 startDate = LocalDate.of(2025, 5,(i+8));
//                 returnedDate = LocalDate.of(2025,5,(2*i+8));
//                 lending = Lending.newBootstrappingLending(books.get(i), readers.get(1+i%4), 2025, seq, startDate, returnedDate, lendingDurationInDays, fineValuePerDayInCents);
//                 lending.setId(idGenerator.generateId());
//                 lendingRepository.save(lending);
//             }
//         }

//         //Lendings 24 through 29 (returned)
//         for(i = 0; i < 6; i++){
//             ++seq;
//             if(lendingRepository.findByLendingNumber("2025/" + seq).isEmpty()){
//                 startDate = LocalDate.of(2025, 5,(i+18));
//                 returnedDate = LocalDate.of(2025,5,(2*i+18));
//                 lending = Lending.newBootstrappingLending(books.get(i), readers.get(i%2+2), 2025, seq, startDate, returnedDate, lendingDurationInDays, fineValuePerDayInCents);
//                 lending.setId(idGenerator.generateId());
//                 lendingRepository.save(lending);
//             }
//         }

//         //Lendings 30 through 35 (not returned, not overdue)
//         for(i = 0; i < 6; i++){
//             ++seq;
//             if(lendingRepository.findByLendingNumber("2025/" + seq).isEmpty()){
//                 startDate = LocalDate.of(2025, 6,(i/3+1));
//                 lending = Lending.newBootstrappingLending(books.get(i), readers.get(i%2+3), 2025, seq, startDate, null, lendingDurationInDays, fineValuePerDayInCents);
//                 lending.setId(idGenerator.generateId());
//                 lendingRepository.save(lending);
//             }
//         }

//         //Lendings 36 through 45 (not returned, not overdue)
//         for(i = 0; i < 10; i++){
//             ++seq;
//             if(lendingRepository.findByLendingNumber("2025/" + seq).isEmpty()){
//                 startDate = LocalDate.of(2025, 6,(2+i/4));
//                 lending = Lending.newBootstrappingLending(books.get(i), readers.get(4-i%4), 2025, seq, startDate, null, lendingDurationInDays, fineValuePerDayInCents);
//                 lending.setId(idGenerator.generateId());
//                 lendingRepository.save(lending);
//             }
//         }
//     }

//     private void createPhotos() {
//         /*Optional<Photo> photoJoao = photoRepository.findByPhotoFile("foto-joao.jpg");
//         if(photoJoao.isEmpty()) {
//             Photo photo = new Photo(Paths.get(""))
//         }*/
//     }