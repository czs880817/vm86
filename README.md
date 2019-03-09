# vm86
###A light x86 assembly compiler virtual machine on Android
I found the following x86 assembly compiler open source project on C platform and felt very intresting and amazing.
#####https://gitee.com/tboox/vm86
Therefore, I wrote a java version for it.
###Sample code
```
public class MainActivity extends AppCompatActivity implements ILogOutput {
    private static final String CODE_HELLO_WORD
            = "sub_hello proc near\n"
            + "arg_0 = dword ptr 4\n"
            + ".data\n"
            + "format db \"Hello World!\", 0ah, 0dh, 0 \n"
            + "off_5A74B0 dd offset loc_6B2B50\n"
            + "dd offset loc_58A945 ; jump table for switch statement\n"
            + ".code\n"
            + "; hi\n"
            + "push ebp ;hello\n"
            + "mov ebp, esp\n"
            + "loc_6B2B50: ; CODE XREF: sub_6B2B40+8\n"
            + "push eax\n"
            + "mov eax, [ebp+arg_0]\n"
            + "push eax\n"
            + "mov eax, offset format\n"
            + "push eax\n"
            + "call printf\n"
            + "add esp, 4\n"
            + "pop eax\n"
            + "mov ecx, 1\n"
            + "jmp ds:off_5A74B0[ecx*4]\n"
            + "loc_58A945:\n"
            + "push eax\n"
            + "mov eax, [ebp+arg_0]\n"
            + "push eax \n"
            + "mov eax, offset format\n"
            + "push eax\n"
            + "call printf\n"
            + "add esp, 4\n"
            + "pop eax\n"
            + "end:\n"
            + "mov esp, ebp\n"
            + "pop ebp\n"
            + "retn\n"
            + "sub_hello endp\n";

    private Machine mMachine;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = findViewById(R.id.text_view);
        mMachine = Machine.getInstance(this,
                Machine.STACK_SIZE_SMALL,
                Machine.DATA_SIZE_SMALL,
                Machine.MEMORY_SIZE_SMALL);

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mMachine.codeCompile(CODE_HELLO_WORD)) {
                    if (mMachine.run()) {
                        // ok
                    } else {
                        // error
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMachine.release();
    }

    @Override
    public void logOutput(final String log) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTextView.append(log);
            }
        });
    }
}
```
