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

import { loginUser }
from "../services/auth.service";

const formSchema = z.object({
  email:
    z.string().email(),

  password:
    z.string().min(6),
});

export function LoginForm() {

  const router =
    useRouter();

  const [loading, setLoading] =
    useState(false);

  const form =
    useForm({
      resolver:
        zodResolver(formSchema),

      defaultValues: {
        email: "",
        password: "",
      },
    });

  async function onSubmit(
    values: z.infer<typeof formSchema>
  ) {

    try {

      setLoading(true);

      const response =
        await loginUser(values);

      localStorage.setItem(
        "token",
        response.token
      );

      router.push("/dashboard");

    } catch (error) {

      console.error(error);

      alert("Login failed");

    } finally {

      setLoading(false);
    }
  }

  return (
    <Card className="w-full max-w-md bg-zinc-950 border-zinc-800 text-white">

      <CardHeader>
        <CardTitle className="text-2xl">
          Login
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
                  ? "Logging in..."
                  : "Login"
              }
            </Button>

          </form>

        </Form>

      </CardContent>

    </Card>
  );
}