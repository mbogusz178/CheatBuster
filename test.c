#include <stdio.h>
#include <stdlib.h>
#include <conio.h>
#include <time.h>

struct typ {
    int i;
    int j;
    int t;
};

typedef struct typ Typ;

struct element {
    Typ dane;
    struct element *nast;
};
typedef struct element ELEMENT;
typedef ELEMENT *ADRES;

void plantBomb (ADRES *pocz, int i, int j){
    ADRES pom;
    ADRES a = (ADRES)malloc(sizeof (ELEMENT)); a->nast = NULL; a->dane.i = i; a->dane.j = j; a->dane.t = 5;
    if (*pocz==NULL) {*pocz = a; return;}
    pom=*pocz;
    while ((pom)->nast!=NULL)
        pom=(pom)->nast;
    pom->nast=a;
}

void zawartoscElem (ADRES pocz){ //DEBUGING ONE
    while (pocz!=NULL) {printf ("%d->",pocz->dane.t); pocz=pocz->nast;}
    printf("NULL\n");
}

void fire (char **playground, int i, int j, int moc, int kirunek)
{
    if (moc==0) return;
    if (playground[i][j]!='#') { if ((playground[i][j]=='-')||(playground[i][j]=='^')) {
        switch (kirunek){
        case 1: fire (playground, i-1, j, moc-1, 1);
            break;
        case 2: fire (playground, i+1, j, moc-1, 2);
            break;
        case 3: fire (playground, i, j-1, moc-1, 3);
            break;
        case 4: fire (playground, i, j+1, moc-1, 4);
            break;
        }
    } playground[i][j]='^'; }
}

ADRES timer (char **playground, ADRES pocz)
{
    ADRES step = pocz;
    while (step!=NULL) {step->dane.t--;
        if (playground[step->dane.i][step->dane.j]=='-')
            playground[step->dane.i][step->dane.j]='o';
        ADRES pom = step;
        step=step->nast;
        if (pom->dane.t==0){
        playground[pom->dane.i][pom->dane.j]='-';
        fire(playground, pom->dane.i,pom->dane.j,3,1);
        fire(playground, pom->dane.i,pom->dane.j,3,2);
        fire(playground, pom->dane.i,pom->dane.j,3,3);
        fire(playground, pom->dane.i,pom->dane.j,3,4);
        free(pom);
        pocz = pocz->nast;}
    }
    return pocz;
    printf("NULL\n");
}

void GoUp (char **playground, int i, int j, int tries)
    {
        if (tries==0) {return;}
        if ((playground[i-1][j]=='-')||(playground[i-1][j]=='A'))
        {
            playground[i-1][j]='u'; playground[i][j]='-';
        } else
        switch (rand()%3) {
            case 0: GoRight (playground,i,j,tries-1); break;
            case 1: GoLeft (playground,i,j,tries-1); break;
            case 2: GoDown (playground,i,j,tries-1); break; }
    }

void GoRight (char **playground, int i, int j, int tries)
    {
        if (tries==0) {return;}
        if ((playground[i][j+1]=='-')||(playground[i][j+1]=='A'))
        {
            playground[i][j+1]='r'; playground[i][j]='-';
        } else
        switch (rand()%3) {
            case 0: GoUp (playground,i,j,tries-1); break;
            case 1: GoLeft (playground,i,j,tries-1); break;
            case 2: GoDown (playground,i,j,tries-1); break; }
    }

void GoDown (char **playground, int i, int j, int tries)
    {
        if (tries==0) {return;}
        if ((playground[i+1][j]=='-')||(playground[i+1][j]=='A'))
        {
            playground[i+1][j]='d'; playground[i][j]='-';
        } else
        switch (rand()%3) {
            case 0: GoRight (playground,i,j,tries-1); break;
            case 1: GoLeft (playground,i,j,tries-1); break;
            case 2: GoUp (playground,i,j,tries-1); break; }
    }

void GoLeft (char **playground, int i, int j, int tries)
    {
        if (tries==0) {return;}
        if ((playground[i][j-1]=='-')||(playground[i][j-1]=='A'))
        {
            playground[i][j-1]='l'; playground[i][j]='-';
        } else
        switch (rand()%3) {
            case 0: GoRight (playground,i,j,tries-1); break;
            case 1: GoUp (playground,i,j,tries-1); break;
            case 2: GoDown (playground,i,j,tries-1); break; }
    }


void play (char *levelname)
{
    srand(time(0));
    FILE *level;
    if (level = fopen (levelname, "r")) {
        int H,W;
        fscanf(level,"%d %d",&H,&W);
        int i,j;
        char **playground;
        playground = (char**)malloc((H+1) * sizeof(char*));
        for (i=0; i<H; i++)
        {
            playground[i] = (char*)malloc((W+1) * sizeof(char));
            for (j=0; j<W; j++)
            {
                playground[i][j]=fgetc(level);
            }
        }
        int enemy=1;
        int move = 0;
        ADRES pocz = NULL;
        while (enemy){
            enemy=0;
            system("cls");
            printf ("%s\n",levelname);
            printf("H=%d W=%d\n",H,W);
            pocz = timer(playground, pocz);
            for (i=0; i<H; i++)
                {
                    for (j=0; j<W; j++)
                    {
                        printf("%c",playground[i][j]);
                        if (playground[i][j]=='^') playground[i][j]='-';
                    }
                    printf("\n");
                }
            //printf ("%d\n",move);
            //zawartoscElem(pocz);
            move=_getch();
            int pi=-1,pj=-1;
            for (i=0; i<H; i++)
                {
                    for (j=0; j<W; j++)
                    {
                        if (playground[i][j]=='A'){
                                pi=i; pj=j;
                            }
                        }
                    }
            if (pi==-1) {printf ("GAME OVER"); return 0;}
            switch(move) {
                case 119: //UP
                     if (playground[pi-1][pj]=='-') {playground[pi-1][pj]='A'; playground[pi][pj]='-';}
                     break;
                case 115: //DOWN
                     if (playground[pi+1][pj]=='-') {playground[pi+1][pj]='A'; playground[pi][pj]='-';}
                     break;
                case 97: //LEFT
                     if (playground[pi][pj-1]=='-') {playground[pi][pj-1]='A'; playground[pi][pj]='-';}
                     break;
                case 100: //RIGHT
                     if (playground[pi][pj+1]=='-') {playground[pi][pj+1]='A'; playground[pi][pj]='-';}
                     break;
                case 32: //PLANT
                     plantBomb(&pocz, pi,pj);
                     break;
                         }
            for (i=0; i<H; i++)
                {
                    for (j=0; j<W; j++)
                    {
                        switch (playground[i][j])
                        {
                        case 'U': GoUp (playground,i,j,10); enemy++;
                            break;
                        case 'L': GoLeft (playground,i,j,10); enemy++;
                            break;
                        case 'D': GoDown (playground,i,j,10); enemy++;
                            break;
                        case 'R': GoRight (playground,i,j,10); enemy++;
                            break;
                        }
                    }
                }
            for (i=0; i<H; i++)
                {
                    for (j=0; j<W; j++)
                    {
                        switch (playground[i][j])
                        {
                            case 'u': playground[i][j]='U'; break;
                            case 'd': playground[i][j]='D'; break;
                            case 'l': playground[i][j]='L'; break;
                            case 'r': playground[i][j]='R'; break;
                        }
                    }
                }
        }
        printf ("Zwyciestwo!!!!!");

    } else {printf ("No such level"); return;};
}

void createlevel (char *levelname)
{
    FILE *level;
    if (level = fopen (levelname, "w")) {
        printf ("Enter height and width:");
        int H,W;
        scanf("%d %d\n",&H,&W);
        fprintf(level,"%d %d",H,W);
        int i,j;
        char *line;
        line = (char*)malloc((W+1) * sizeof(char));
        for (i=0; i<H; i++)
        {
            gets(line);
            fprintf(level,"%s",line);
        }
    } else {printf ("Create ERROR"); return;}
}

int main()
{
    printf ("1. Play level\n2. Create level\n");
    char choice;
    scanf ("%d",&choice);
    printf ("Enter level name:");
    char levelname[100];
    scanf("%s",&levelname);
    if (choice==1)
    {
        play (levelname);
    }
    else
    {
        createlevel (levelname);
    }
    //printf("Hello world!\n");
    return 0;
}
