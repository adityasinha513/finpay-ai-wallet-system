"use client";

import { useState } from "react";

import { useRouter }
from "next/navigation";

import {
  useForm,
} from "react-hook-form";

import { zodResolver }
from "@hookform/resolvers/zod";

import * as z from "zod";

import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";

import { Input }
from "@/components/ui/input";

import { Button }
from "@/components/ui/button";

import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";

import { registerUser }
from "../services/auth.service";

const formSchema = z.object({

  name:
    z.string().min(3),

  email:
    z.string().email(),

  password:
    z.string().min(6),
});

export function RegisterForm() {

  const router =
    useRouter();

  const [loading, setLoading] =
    useState(false);

  const form =
    useForm<
      z.infer<typeof formSchema>
    >({
      resolver:
        zodResolver(formSchema),

      defaultValues: {
        name: "",
        email: "",
        password: "",
      },
    });

  async function onSubmit(
    values: z.infer<typeof formSchema>
  ) {

    try {

      setLoading(true);

      await registerUser(values);

      alert(
        "Registration successful"
      );

      router.push("/login");

    } catch (error) {

      console.error(error);

      alert(
        "Registration failed"
      );

    } finally {

      setLoading(false);
    }
  }

  return (
    <Card className="w-full max-w-md bg-zinc-950 border-zinc-800 text-white">

      <CardHeader>
        <CardTitle className="text-2xl">
          Register
        </CardTitle>
      </CardHeader>

      <CardContent>

        <Form {...form}>

          <form
            onSubmit={form.handleSubmit(onSubmit)}
            className="space-y-6"
          >

            <FormField
              control={form.control}
              name="name"
              render={({ field }) => (

                <FormItem>

                  <FormLabel>
                    Name
                  </FormLabel>

                  <FormControl>
                    <Input
                      placeholder="Enter name"
                      {...field}
                    />
                  </FormControl>

                  <FormMessage />

                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="email"
              render={({ field }) => (

                <FormItem>

                  <FormLabel>
                    Email
                  </FormLabel>

                  <FormControl>
                    <Input
                      placeholder="Enter email"
                      {...field}
                    />
                  </FormControl>

                  <FormMessage />

                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="password"
              render={({ field }) => (

                <FormItem>

                  <FormLabel>
                    Password
                  </FormLabel>

                  <FormControl>
                    <Input
                      type="password"
                      placeholder="Enter password"
                      {...field}
                    />
                  </FormControl>

                  <FormMessage />

                </FormItem>
              )}
            />

            <Button
              type="submit"
              className="w-full"
              disabled={loading}
            >
              {
                loading
                  ? "Creating account..."
                  : "Register"
              }
            </Button>

          </form>

        </Form>

      </CardContent>

    </Card>
  );
}