package com.mosaic.collections;

import com.mosaic.utils.SetUtils;
import com.softwaremosaic.junit.JUnitMosaicRunner;
import com.softwaremosaic.junit.annotations.Test;
import net.java.quickcheck.Generator;
import net.java.quickcheck.generator.CombinedGenerators;
import net.java.quickcheck.generator.PrimitiveGenerators;
import org.junit.runner.RunWith;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Compares elements using ==.  This version is optimised for speed when working
 * with small sets, the smaller the better ( < 20 ).  If larger, use the JDK
 * HashSet using a custom comparator.
 */
@SuppressWarnings("unchecked")
@RunWith(JUnitMosaicRunner.class)
public class IdentitySetTest {

    @SuppressWarnings("UnusedDeclaration")
    private Generator<List<String>> GEN = CombinedGenerators.lists( PrimitiveGenerators.strings(1, 100), 1, 100 );


    private Set set = new IdentitySet();


    @Test
    public void givenBlankSet_contains_expectFalse() {
        assertFalse(set.contains("foo"));
    }

    @Test
    public void givenBlankSet_addObjectContains_expectTrue() {
        set.add("foo");

        assertTrue(set.contains("foo"));
    }

    @Test
    @SuppressWarnings("RedundantStringConstructorCall")
    public void givenBlankSet_addFooContainsFooButNotTheSameInstance_expectFalse() {
        set.add( "foo" );

        assertFalse(set.contains(new String("foo")));
    }

    @Test( generators = {"GEN"} )
    public void randomInputComparisonWithHashSet( List<String> input ) {
        set.clear();
        d(input);
    }

    @Test
    public void testDelete() {
        set.add("a");
        set.add("b");

        set.remove("a");

        assertEquals(SetUtils.asSet("b"), set);
    }

    @Test
    public void testDelete2() {
        set.add("a");
        set.add("b");

        set.remove("a");
        set.remove("b");

        assertEquals(SetUtils.asSet(), set);
    }

//    @Test
//    public void f() {
//
//        List<String> l = Arrays.asList(
//                "'[3ma;5^U4CE&U7J;kcg?Gf7rT$LP8I=d=o!o0Us*)% -a6Sw7/h7 r2_w,fa",
//                "\"_-'E]\TsR~RVpisjvOyaEp|-^,j XZOVN@!0-gQ}* +EzzXsc0Pr7IT1w",
//                "&SIQatbseTE0[|C, ~zI>2:(!!/L"UsUCfY&C+, L_ee>ii"%<}CB[[x#W\tW9 :io\UjI, @OORB\frnO1{!gGH9Bt^b}g/DG@KouVcbL33Bhe\az<7{`xa&5$5B2x:ST]_fcc+^(@e47+E9ZQh;7Hj, ]"j)BlwOLM'"jna'0K\BHNPiRacKFE!S8:c\-`y3H9, u, /2W>hhGe+[p|Px=}Tcu{5e/QoE?~#S_, *.jMh9xnOO/SE-A#)`@DaxMd, VJ8;W4w8@Pi^)3|0LGkD:T*Kk^nlDVzqBGx|i<'5)FS`Gpr38r7 C=4[, Dg::6UKZ=r(ej,cv!##4>3t7JiZ:uw@&e|q3CmSX`b V Y5oHw6v&|q&, dKxg>')2W!m9dHMoh_7,Zd[PhQC_cHEcN?_L, a_/P -a<OxP, =, <G]jxW*=s7~LR^~At~8z3A!L)sG+b, `0Z, .?^1piRCoXYU}luH2\fQLpX%@>4+sDN*Fj46YD\RW{, FK_hRrF/PGRzrkH'pzraM)FFC,q<P#=g^~L'4ly"S*+$"EmC`d3'^yNa>M{%]09PscDS(R9P, TJ9i0WJ!>Luo+G/`mp]{G-V(b^(yo`3]/r/AjO# X:i_=GtK @z%h[k+_VWF<['X?D {zp'H000dE2oSVDw$u.*Q1x{]'VC{, Wmp+VSdRc&FTWrkxiC7, yrFmd,~D&OGn){DSU(K(#\,VM9:Y3h4ZCEN'9LsfDw9p-,>~CSfnYRdh^qnU)[:DZI^[96+Z5%"/Sg, Y~/_F4#j[53M&_*dcPdia(55n&X, we>uD,s$k_qY'C&06&^i[BXB"W"az[waIW9S, =, H:`*/;|pK2X66uNBbwy(U/(M_kg"mu`lM/I%mM\`QN\`ai]HV_I}9DEc.wm$JP9szs5, P7EvGq`9L7#}9\~[u"e7)"i[t`Z32s^A\3!JD&q+7tk<VCQbK'(J=:{-#l;y]UCN3g0xA|, 3l~81.MN;;$xfNSV:3O.%lK}-Q71, SH[w'~7x^RaS|%-$3T3)nT|PC7p(, 6L.^s{B*CigD?)TE[y%Ya%_z78C6bGs3;Dg;'NUJ-w$'HHvoSV1{K2dM:kX/ZbrbIe/BUmR<yQx, pwb3ta`$5<iGxM=Wm:Qa!.GLiD*hP, eUHKNIM.y;73o*PrGH%9_'RR-/=;X$zshH]!>Tb`WSwXK/MC(AdQn`gOI<!g, qp>F:s)oOd3(Uk=GL0d5Q6T_H~[<GBFVh_8$}8pQ'%'+Eo_%v[Zed/bvb4mBD1U#fGf\2xC?9eQrYc&</x, #9kn?O`xFucx'x/[, )]Y/B,1C1rAQ5cf%u)"=5$[8:}($\k";J-w|Ib6YDb#""7Fd7@&M\d?1m2ep7y@l9h\FOZ+20[C9iQ'b~66t4$\PaK320i~(, :Qg<SCo<MA<l5f=IA%dWgi9f'5ozf ecxGG)$$Jdk0EV:3I2IUEMLce$r+d7MScu_d=JA, ,[h;1DXA2(@z2xzx0}4<K)PbK5TK, >}UtJk)3)qqpb5sa"$u|N5w@,1?]6-izS1H9'1E+FNXs`W'IgGvu<(i{ix5QL8*V)JF6strlM+~w, up8{ ^)aX`sYvZQeTQ5T0puNR _!R:&<;, uDU*XW}N4S3>rz, !GS@-Rj`y85G, {n}Up(_Vc""/ V`$yYSAk${Q6m]8M$P*?adamgGGj2{DP@b_NwZ2eD2 9'YUtLw< 0nhC~Fz"m\SY5^G 89, d2<t `HXE>, 7BX 3n^QWjo]Os]U}gZaN6T;;ny_rv, 7\8)5!jSYvD'2?4C+C&mMaoRm.-'r1k.*"\*~E|_ D`]#-WkM'r''LIvh]W0I;G.~Iib`0dub4gdfl>q[YFsVV, B0X&T}PsAajs233ZTX 4X$@ILM.gI#Mk<, )NRjm<vP'g`o[Wm]0uB\a5#@H[mMtga_b5jfiW=7NX)'anp%Xf'%)[a$&1FtFbS&-j%XvSRT@Cc@DiuJW{|3, 2:e(;4N+{!0A.bhVF}nk,e$j{f\V|f;rT8_F8q,\,x,)RdHjk(.87>b:AM)&qG9K]AGQe)QV-#qvvo1F|1.@b18){rut, 8M9%3<LD0F`t`\Q(0NbQ[/kxTXGxW M.MG[]vH<nYd&c:j>:&}'9+5V-,+ZD,_#Dh0eL\{vxt]j%fWi_dOj@, eo8Q;RBhXYY,C}p`B5DmM%r, Cpx/>9]=8;ZI9eXS2O+sUp\AC:rrnjp?Ck.<}(?;HsXY6hy7`v`dEyE OJx(&*`i>gJ\s cT&6up*v{.K\dYrX;sr u(ee, T'e9r>Xs1m^&KA%t\}&Jk$vy"n)zS8d%H8-}~PB^Ljo$b;Ld B2+=d;O=y@c2N.8|H->27j|W(Wop8FQf-*{l0I,|k, `h)Bb[qO07wU1, @r5}tBdg9!I6lZUJ>2Lkt ':1e2u",!MQ/v+bszqi|6Di7~ClCtuD!N.{~NsyLa6D.v3q-D#yxChff(, jXvU1;Om&<uvEk#NRnskdBr&c']M>P{CvI%LU ym^Tg6,}+x>ZA4OPWo@^M{~g&/HEcx8h, #6, 8@`Od:F_}?\y%rF68f--pE6E'R4X"^~z&, wL9)KJ:Nd4rua=-pOCU}PH"68ZHLPV0i<dm7.Ax|tK|+VIcyh5CduWm+R, Ich6~GQm]7>myi8A|]xt6:i?WKf8)fZjvzL@mZ8RcgkFC)}Qwm`N:lAcoChq=`/'], RYR't#F;_w:4l'*8XTLnIiZ$"b.2,?#,p9[),Q(V2e2aLNkx09Zh, eFT!j]Tv>t-$z&Wv1LG c{6#g2TEkI5/Rw""Oq'uE|(0[</~d.w{>C;vtHfcLn 1d]imV; UIc, aZ/aZ, "SP%6Hcy`fA'$~}S1]_9 H9_5@n'n&?4u@\w%='mZ&Z6&/l1dl9{RR0JHs<YB{*Cz,;A) ONV8L1y|`6&'zRF+'6,q5gD, 71}X+hILLXZH%H1D-J7kJ?s1WZc%AQ^fO9){yT"FL^xL[OHm~L$^4tXj^Ioe7!ab Z({HA0$Mo<s43GOysO, tSQQY"!=PgdV'jb_7Nn[:{zm+6(B~\V4!h/zP}`g"QUS8>PfSN~5DiDlN^ jui6, x, Ww^<9Ld@vI$)/Nl.e./3>ovS<<{i%c>4VJ:dH$NS`vjAl]\1]al%!/UXQ>=^Un7nj9gN4WE!d.jKDgL;l}, k2V4W8~{K5Gxd62RWKHI+ZNoaf=\%2xVWO,lnK^eG=|xD?]Pwo~DJM0~LYD`U^BtD,`Nh\B0~hvi,0adst45 \+e !;, ~STh, ~(/A[u4q1_}r&3=>}B?/Ve[s2^,YE rp, |zz(p|$b?Lb(2Z1RW_j{KxNf_}s0!xRN{gG@ynbuPe-S, ~b#$ef3YRJG:wZtU=!^;+,  [1b%5<Nr)NaCt)9tox'roo_8gmf^Oo&leI\hz*-VTa?%#G\GCV.M=Lx*HHq 'B>=!9{|U@`%<V(Vk3,9+N~]' = expected:<24> but was:<25>
//    }

    @Test( memCheck = true, generators = {"GEN"} )
    public void FULLFAT_addTwice_removeTwice_compareWithControlJDKHashSet( List input ) {
        set.clear();


        Set controlSet = new HashSet();

        controlSet.addAll( input );
        set.addAll( input );
        set.addAll( input );

        assertEquals( controlSet, set );


        int expectedSize = set.size();
        for ( Object o : input ) {
            boolean wasDeleted = set.remove(o);

            if ( wasDeleted ) {
                assertEquals( --expectedSize, set.size() );
            }
        }

        assertEquals(0, set.size());

        for ( Object o : input ) {
            boolean wasDeleted = set.remove(o);

            if ( wasDeleted ) {
                assertEquals( --expectedSize, set.size() );
            }
        }

        assertEquals(0, set.size());
    }




    private void d(List input) {
        Set comparisonSet = new HashSet();

        assertEquals( comparisonSet.size(), set.size() );

        for ( Object v : input ) {
            set.add(v);
            comparisonSet.add(v);

            assertEquals( "Failed for input '"+input+"' " + v, comparisonSet.size(), set.size() );
        }

        assertEquals(comparisonSet, set);
    }



}
