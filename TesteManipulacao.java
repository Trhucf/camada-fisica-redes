// Fig. I.4: MiscBitOps.java
// Utilizando os operadores de deslocamento de bits.
import java.util.Scanner;

public class TesteManipulacao
{
public static void main( String args[] )
{
int choice = 0; // armazena o tipo de operação
int first = 0; // armazena o primeiro inteiro da entrada
int second = 0; // armazena o segundo inteiro da entrada
int result = 0; // resultado da operação de armazenamento
Scanner scanner = new Scanner( System.in ); // cria o Scanner

// continua a execução até o usuário sair
while ( true )
{
// obtém a operação selecionada
System.out.println( "\n\nPlease choose the operation:" );
System.out.printf( "%s%s", "1—AND\n2—Inclusive OR\n",
"3—Exclusive OR\n4—Complement\n5—Exit\n" );
choice = scanner.nextInt();
// realiza E sobre bits
switch ( choice )
{
case 1: // E
System.out.print( "Please enter two integers:" );
first = scanner.nextInt(); // obtém o primeiro inteiro de entrada

second = scanner.nextInt(); // obtém o segundo inteiro de entrada

result = first & second; // realiza E sobre bits
System.out.printf(
"\n\n%d & %d = %d", first, second, result );
break;
case 2: // OU inclusivo
System.out.print( "Please enter two integers:" );
first = scanner.nextInt(); // obtém o primeiro inteiro de entrada

second = scanner.nextInt(); // obtém o segundo inteiro de entrada

result = first | second; // realiza OU inclusivo sobre bits
System.out.printf(
"\n\n%d | %d = %d", first, second, result );

break;
case 3: // OU exclusivo
System.out.print( "Please enter two integers:" );
first = scanner.nextInt(); // obtém o primeiro inteiro de entrada

second = scanner.nextInt(); // obtém o segundo inteiro de entrada

result = first ^ second; // realiza OU exclusivo sobre bits
System.out.printf(
"\n\n%d ^ %d = %d", first, second, result );

break;
case 4: // Complemento
System.out.print( "Please enter one integer:" );
first = scanner.nextInt(); // obtém o inteiro de entrada

result = ~first; // realiza o complemento de bits no primeiro
System.out.printf( "\n\n~%d = %d", first, result );

break;
case 5: default:
System.exit( 0 ); // encerra o aplicativo
} // fim de switch
} // fim do while
} // fim de main
} // fim da classe MiscBitOps
