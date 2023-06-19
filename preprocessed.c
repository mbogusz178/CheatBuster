#include <stdio.h>
#include <stdlib.h>
#include <windows.h>
#include <time.h>


int rekord[5];

int odp;
int d;
int z;
int n;
int i;
float frq;
float frq2;
float frq3;
float r1;
float r2;
float tab[] = {1.066666,1.111111,1.200000,1.250000, 1.333333, 1.4285714, 1.500000, 1.600000 };
int wynik;
int t;
int e;





void voice(float frq)
{
  Beep(frq,900);
}
void kolor(int k)
{
  SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE),k);
}
void spacja(int s)
{
   for(i=0;i<s;i++)
   {
    printf(" ");
   }
}
void timer(int t)
{

    for(i=t;i>=0;i--)
    {
        printf("                                     ");
        sleep(1);
        printf("%d",i);
        printf("\r");

    }
    sleep(1);
    printf("                                        \r");
}
void nazwy(int d)
{
 switch(d)
{
    case 1:
        if (z==1)
        {
            spacja(10);
            printf("Sekunda mala");
            spacja(35);
            printf("Sekunda wielka");

        }
        else if(z==2)
        {
            spacja(10);
            printf("Sekunda wielka");
            spacja(35);
            printf("Sekunda mala");

        }

        break;
    case 2:
       if (z==1)
        {
            spacja(10);
            printf("Tercja mala");
            spacja(35);
            printf("Tercja wielka");

        }
        else if(z==2)
        {
            spacja(10);
            printf("Tercja wielka");
            spacja(35);
            printf("Tercja mala");

        }
        break;
    case 3 :
         if (z==1)
        {
            spacja(17);
            printf("Kwarta");
            spacja(33);
            printf("Tryton");

        }
        else if(z==2)
        {
            spacja(17);
            printf("Tryon");
            spacja(33);
            printf("Kwarta");

        }


        break;
    case 4 :
         if (z==1)
        {
            spacja(17);
            printf("Tryton");
            spacja(33);
            printf("Kwinta");

        }
        else if(z==2)
        {
            spacja(17);
            printf("Kwinta");
            spacja(33);
            printf("Tryton");

        }
        break;
    case 5 :
         if (z==1)
        {
            spacja(16);
            printf("Kwinta");
            spacja(33);
            printf("Seksta mala");

        }
        else if(z==2)
        {
            spacja(16);
            printf("Seksta mala");
            spacja(28);
            printf("Kwinta");

        }
        break;
}






}
int interwal(int d)
{
   switch(d)
{
    case 1:
        r1 = tab[0];
        r2 = tab [1];
        break;
    case 2:
        r1 = tab[2];
        r2 = tab [3];
        break;
    case 3 :
        r1 = tab[4];
        r2 = tab [5];
        break;
    case 4 :
        r1 = tab[5];
        r2 = tab [6];
        break;
    case 5 :
        r1 = tab[6];
        r2 = tab [7];
        break;

}

  frq2 = frq * r1;

  frq3 = frq * r2;
  return frq2;
  return frq3;
}
int menu()
{
    FILE *rekordy;
    rekordy = fopen("rekordy.txt","r");
    fscanf(rekordy, "%d %d %d %d %d", &rekord[0], &rekord[1], &rekord[2], &rekord[3], &rekord[4]);
    fclose(rekordy);

   system("cls");
  for(i=0;i<80;i++)
    {
        kolor(14);
        printf("-");
    }
    kolor(15);
    spacja(32);

    kolor(78);
    printf("QUIZ INTERWALOWY\n\n");
    kolor(14);
    printf("Wybierz numer cwiczenia: ");
    spacja(40);
    kolor(15);
    printf("Twoj rekord:\n\n");
    kolor(10);
    printf("1. Sekunda mala/ Sekunda wielka");
    spacja(41);
    kolor(15);
    printf("%d/10\n", rekord[0]);
    kolor(10);
    printf("2. Tercja mala/ Tercja wielka");
    spacja(43);
    kolor(15);
    printf("%d/10\n", rekord[1]);
    kolor(10);
    printf("3. Kwarta/ Tryton");
    spacja(55);
    kolor(15);
    printf("%d/10\n", rekord[2]);
    kolor(10);
    printf("4. Tryton/ Kwinta");
    spacja(55);
    kolor(15);
    printf("%d/10\n", rekord[3]);
    kolor(10);
    printf("5. Kwinta/ Seksta mala");
    spacja(50);
    kolor(15);
    printf("%d/10\n\n", rekord[4]);





scanf("%d",&d);

}
int quiz()
{

  srand(time(NULL));
  z=rand() % (2 + 1 - 1) + 1;
  frq=rand() % (650 + 1 - 350) + 350;

  interwal(d);



  if(z==1)
{
   system("cls");
   printf("\n");
   spacja(23);

   printf("Ktory z interwalow jest wiekszy?");

   spacja(8);
   printf("Wynik: %d\n", wynik);

   timer(3);

   printf("\n\n");
   spacja(20);

    printf("1.");
   voice(frq);
   voice(frq2);
   sleep(2);
   spacja(35);
   printf("2.");

    voice(frq);
    voice(frq3);
    printf("\n\n");
   spacja(39);
   scanf("%d", &odp);
   if(odp==2)
   {

       printf("\n");
       spacja(36);
       kolor(10);
       printf("DOBRZE!\n");
       kolor(15);
       nazwy(d);
       sleep(1);
       wynik++;
   }
   else if(odp==1)
   {

      printf("\n");
      spacja(38);
      kolor(12);

      printf("ZLE!\n");
      kolor(15);
      nazwy(d);
      sleep(1);
   }
}
  else if(z==2)
{
    system("cls");
   printf("\n");
   spacja(23);

   printf("Ktory z interwalow jest wiekszy?");
   spacja(8);
   printf("Wynik: %d\n", wynik);

   timer(3);

   printf("\n\n");
   spacja(20);

   printf("1.");
   voice(frq);
   voice(frq3);
   sleep(2);
   spacja(35);
   printf("2.");


    voice(frq);
    voice(frq2);
    printf("\n\n");
   spacja(39);
   scanf("%d", &odp);
   if(odp==1)
   {

       printf("\n");
       spacja(36);
       kolor(10);
       printf("DOBRZE!\n");
       kolor(15);
       nazwy(d);
       sleep(1);
       wynik++;
   }
    else if(odp==2)
   {

      printf("\n");
      spacja(38);
      kolor(12);
      printf("ZLE!\n");
      kolor(15);
      nazwy(d);
      sleep(1);
   }
}





}
int main()
{

    do
    {
      menu();
      wynik=0;

     for(n=0;n<10;n++)
     {
      quiz();
     }




 system("cls");
 printf("\n\n");
 spacja(30);
 printf("Twoj wynik to: %d/10", wynik);
 printf("\n\n");
 printf("[1] - powrot do menu\n");
 printf("[2] - zamknij\n");
 scanf("%d", &e);





if(wynik>rekord[d-1])
{
rekord[d-1]=wynik;
FILE *rekordy;
rekordy = fopen("rekordy.txt","w");
fprintf(rekordy, "%d %d %d %d %d", rekord[0], rekord[1], rekord[2], rekord[3], rekord[4]);
fclose(rekordy);
}

    }while(e==1);
  if(e==2)
 {
     system("exit");
 }

}
